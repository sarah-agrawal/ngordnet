Ngordnet

A project for indexing and querying a large text corpus (~300MB) efficiently, with a REST API connecting the backend to a frontend.

What it does:

Ngordnet takes a large text corpus and builds custom data structures on top of it so queries return fast, instead of scanning the whole dataset every time. It exposes that functionality through a REST API built with Jetty, so a frontend can send a query and get results back in real time.

How it works:

Data structures: Custom graph and TreeMap-based structures designed specifically for this dataset's size and access patterns, built from scratch rather than relying on a generic off-the-shelf structure
Backend: Java, with a Jetty server handling REST API requests
Frontend: Sends queries to the backend and displays the results

Tech stack:

Java (core logic, data structures, API)

Jetty (REST API server)

JavaScript (frontend)
