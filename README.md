URL Shortener with Analytics
A production-style URL shortener built to demonstrate backend engineering depth, not just CRUD: cache-aside caching with Redis, an async event pipeline with Kafka and MongoDB, JWT-based auth with ownership checks, and a deployed, containerized service.
Live demo (Swagger UI): https://url-shortener-analytics-vt8r.onrender.com/swagger-ui.html

What it does
Shortens a long URL into a unique code (base62-encoded auto-increment ID) and redirects visitors instantly.
Caches redirects in Redis (cache-aside, 1-hour TTL) to avoid hitting MySQL on every click.
Publishes a click event to Kafka on every redirect — asynchronously, so the redirect never waits on analytics.
A separate Kafka consumer writes click events into MongoDB.
Exposes a protected analytics endpoint (clicks by day, top referrers, top browsers) computed via MongoDB aggregation pipelines, so raw click data never has to be pulled into application memory to be counted.
Secures link creation and analytics with JWT auth, scoped so only a link's creator can view its stats.

                         ┌─────────────┐
   POST /api/urls/shorten│             │
   ───────────────────▶  │ Spring Boot │ ──▶ MySQL (users, url_mapping)
                         │             │
   GET /{code}           │             │
   ───────────────────▶  │             │ ──▶ Redis (cache-aside, 1h TTL)
                         │             │        │ on miss, falls back to MySQL
                         │             │
                         │             │ ──▶ Kafka topic: click-events (async, fire-and-forget)
                         └─────────────┘           │
                                                    ▼
                                          Kafka Consumer (separate service)
                                                    │
                                                    ▼
                                          MongoDB: click_events collection

   GET /api/analytics/{code}  ──▶ ownership check (MySQL) ──▶ MongoDB aggregation pipeline ($facet)

Why two databases? MySQL holds the source-of-truth relational data (users, URL mappings, ownership) — this needs strict uniqueness and relational integrity. MongoDB holds the high-volume, append-only click-event stream, where the schema is expected to evolve and writes vastly outnumber structured queries. Click analytics are computed with MongoDB's aggregation pipeline so only the aggregated result crosses the network — not every raw click document — avoiding loading potentially millions of rows into application memory just to count them.
Tech stack
Technology	                      Role
Spring Boot	                   Core REST API
MySQL	                         Source-of-truth data: users, URL mappings, ownership
Redis	                         Cache-aside layer for redirects, cuts MySQL reads on hot links
Apache Kafka (Confluent Cloud) Async, non-blocking click-event pipeline
MongoDB (Atlas)	               High-volume click-event storage and aggregation
Spring Security + JWT	         Stateless auth, ownership-scoped analytics
Swagger / OpenAPI	             Live, interactive API docs
Docker	                       Multi-stage build for a lean, deployable image
Render	                       Live deployment

Key design decisions
Cache-aside over write-through: redirects are read-heavy and clicks vastly outnumber URL creations, so caching on read (with a TTL for staleness) made more sense than keeping Redis in sync on every write.
Kafka instead of writing analytics directly to MongoDB in the request path: a direct synchronous write would make every redirect wait on an analytics database write, coupling a "must be fast" path to a "nice to have" feature. Kafka decouples them — the redirect publishes and returns immediately; a separate consumer handles the actual write.
Click events keyed by short code: guarantees all events for a given link stay in order within a partition, even though partitions in general don't preserve cross-key ordering.
Ownership via @ManyToOne to User, not a plain username string: lets the database enforce referential integrity instead of relying on application code alone.
Redirects are public; everything else requires a token: a short link only works if anyone can follow it without logging in. Auth protects creation, ownership, and analytics not the redirect itself.

Measured performance
Redirect latency, tested locally with 100 sequential requests via curl:

Scenario	                  Avg. latency
MySQL lookup (cache miss)	   ~2ms
Redis lookup (cache hit)	   ~1ms

Running locally
Clone the repo and set the following environment variables (see .env.example):
DB_URL, DB_USERNAME, DB_PASSWORD — MySQL connection
SPRING_DATA_REDIS_HOST, SPRING_DATA_REDIS_PORT — Redis connection
KAFKA_BOOTSTRAP_SERVERS, KAFKA_JAAS_CONFIG — Kafka (Confluent Cloud) connection
MONGODB_URI — MongoDB Atlas connection
JWT_SECRET — at least 32 characters
Build and run with Docker:
bash
   docker build -t url-shortener .
   docker run -p 8080:8080 --env-file .env url-shortener
Visit http://localhost:8080/swagger-ui.html to explore and test the API.

API overview
Method	    Endpoint	             Auth required	Description
POST	    /api/auth/register	    No	Create an account
POST	    /api/auth/login	        No	Get a JWT
POST	    /api/urls/shorten	      Yes	Create a short link
GET	      /{code}	                No Redirect to the original URL
GET	      /api/analytics/{code}	  Yes (owner only)	View click analytics for a link

Full interactive documentation is available at /swagger-ui.html, both locally and on the live deployment.
