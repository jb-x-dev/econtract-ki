# eContract KI - Production Deployment Guide

**Version:** 4.2  
**Date:** October 29, 2025

---

## 🚀 Quick Start with Docker Compose

This guide provides instructions for deploying the eContract KI application using Docker and Docker Compose. This is the recommended method for a production environment.

### Prerequisites

- A Linux server (Ubuntu 22.04 LTS recommended)
- Docker Engine (latest version)
- Docker Compose (latest version)
- Git (for cloning the repository)
- A registered domain name (optional, for HTTPS)

### Step 1: Clone the Repository

Clone the project repository to your server:

```bash
git clone <your-repository-url>
cd econtract-ki
```

### Step 2: Configure Environment Variables

Copy the example `.env` file and customize it with your own secure passwords and settings:

```bash
cp .env.example .env
nano .env
```

**Important:** Change the default passwords for `MYSQL_ROOT_PASSWORD` and `MYSQL_PASSWORD` to strong, unique values.

### Step 3: Build and Start the Application

Use Docker Compose to build the images and start all services in detached mode:

```bash
docker-compose up --build -d
```

This command will:
1.  Build the `econtract-app` Docker image from the `Dockerfile`.
2.  Pull the `mysql:8.0` and `nginx:alpine` images from Docker Hub.
3.  Create and start the `mysql`, `econtract`, and `nginx` services.
4.  Create the necessary Docker volumes and networks.

### Step 4: Verify the Application is Running

Check the status of the running containers:

```bash
docker-compose ps
```

You should see all three services (`mysql`, `econtract`, `nginx`) with a status of `Up (healthy)`.

Check the application logs:

```bash
docker-compose logs -f econtract
```

Look for a line similar to this, indicating the application has started successfully:
`Tomcat started on port(s): 8080 (http) with context path '/econtract'`

### Step 5: Access the Application

Your eContract KI application is now accessible:

- **HTTP:** `http://<your_server_ip>`
- **HTTPS:** `https://<your_domain_name>` (if you configured SSL)

---

## 🔧 Advanced Configuration

### SSL/TLS Certificate (HTTPS)

For production, it is **highly recommended** to use HTTPS. You can obtain a free SSL certificate from [Let's Encrypt](https://letsencrypt.org/).

1.  **Obtain a certificate:**
    Use `certbot` to get a certificate for your domain.

2.  **Copy the certificate files:**
    Copy your `fullchain.pem` and `privkey.pem` files to the `docker/nginx/ssl/` directory.
    - `fullchain.pem` -> `certificate.crt`
    - `privkey.pem` -> `private.key`

3.  **Restart Nginx:**
    ```bash
    docker-compose restart nginx
    ```

### Database Initialization

The `docker/mysql/init/` directory can be used to run SQL scripts on database creation. Any `.sql`, `.sql.gz`, or `.sh` files in this directory will be executed.

### Custom MySQL Configuration

The `docker/mysql/conf/my.cnf` file contains optimized settings for production. You can adjust these values based on your server's resources.

### Nginx Configuration

The `docker/nginx/` directory contains the Nginx configuration.
- `nginx.conf`: Main Nginx configuration.
- `conf.d/econtract.conf`: Site-specific configuration, including the reverse proxy and SSL settings.

---

## ⚙️ Managing the Application

- **Start all services:**
  ```bash
  docker-compose up -d
  ```

- **Stop all services:**
  ```bash
  docker-compose down
  ```

- **Restart a service:**
  ```bash
  docker-compose restart <service_name>
  ```

- **View logs:**
  ```bash
  docker-compose logs -f <service_name>
  ```

- **Update the application:**
  1.  Pull the latest code: `git pull`
  2.  Rebuild and restart: `docker-compose up --build -d`

---

## 📂 Directory Structure

```
/home/ubuntu/econtract-ki
├── docker/
│   ├── mysql/
│   │   ├── init/         # Database initialization scripts
│   │   └── conf/         # Custom MySQL configuration
│   └── nginx/
│       ├── conf.d/       # Nginx site configurations
│       └── ssl/          # SSL certificates
├── src/                  # Application source code
├── target/
│   └── econtract-ki.war  # Compiled application
├── .env.example          # Environment variable template
├── .dockerignore         # Files to ignore in Docker build
├── Dockerfile            # Defines the eContract app image
└── docker-compose.yml    # Defines all services, networks, and volumes
```

---

## Backup and Restore

See `BACKUP.md` for detailed instructions on backing up and restoring your database and uploaded files.

## Monitoring

See `MONITORING.md` for instructions on how to monitor the application's health and performance.

---

## 📞 Support

For questions or issues, please refer to the project's official documentation or contact the development team.

