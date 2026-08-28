# Scale Blog

A modern blog application built with Spring Boot, Thymeleaf, and PostgreSQL.

## Features

- **Landing Page**: Beautiful Medium-style homepage with featured articles
- **Blog Management**: Create, read, and manage blog posts
- **PostgreSQL Support**: Serverless PostgreSQL database integration with Scaleway
- **Responsive Design**: Works on all devices

## Prerequisites

- Java 21+
- PostgreSQL database (local or cloud)

## Configuration

### Development (H2 In-Memory Database)
The application uses H2 database by default for development. Just run:

```bash
./gradlew bootRun
```

Access the H2 console at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:blogdb`
- Username: `sa`
- Password: `tiger`

### Production (PostgreSQL)

1. Create a `.env` file from the template:
```bash
cp .env.example .env
```

2. Edit `.env` with your Scaleway PostgreSQL credentials:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_INSTANCE_ID.db.scaleway.com:5432/YOUR_DB_NAME?sslmode=require
SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME
SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
```

3. Run with production profile:
```bash
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

## Scaleway Serverless PostgreSQL Setup

1. **Create a PostgreSQL instance**:
   - Go to Scaleway Console → Serverless → PostgreSQL
   - Click "Create Instance"
   - Choose your region and settings
   - Note down: Instance ID, Database Name, Username, Password

2. **Connection URL format**:
   ```
   jdbc:postgresql://YOUR_INSTANCE_ID.db.scaleway.com:5432/YOUR_DB_NAME?sslmode=require
   ```

3. **Environment Variables**:
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_INSTANCE_ID.db.scaleway.com:5432/YOUR_DB_NAME?sslmode=require
   export SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME
   export SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD
   ```

## Docker Deployment

### Build the image:
```bash
./gradlew bootBuildImage
```

### Run with Docker:
```bash
docker run -d \
  --name scale-blog \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_INSTANCE_ID.db.scaleway.com:5432/YOUR_DB_NAME?sslmode=require \
  -e SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME \
  -e SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD \
  -e SPRING_PROFILES_ACTIVE=prod \
  rg.fr-par.scw.cloud/funcscwnsfocusedjemisonupa8qmlq/scale:latest
```

## Endpoints

| Path | Description |
|------|-------------|
| `/` | Landing page with featured articles |
| `/blog/posts` | List all blog posts |
| `/blog/posts/{id}` | View a specific post |
| `/blog/posts/new` | Create a new post |

## Project Structure

```
scale/
├── src/
│   ├── main/
│   │   ├── java/eu/bilch/scale/
│   │   │   ├── controller/       # Controllers
│   │   │   ├── model/            # Entity classes
│   │   │   ├── repository/       # JPA repositories
│   │   │   └── PostService.java  # Business logic
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       │   ├── blog/         # Blog templates
│   │       │   └── index.html    # Landing page
│   │       └── application.yaml  # Configuration
│   └── test/                    # Tests
├── build.gradle.kts             # Build configuration
└── README.md                    # This file
```

## Technologies

- **Spring Boot 4.1.1**
- **Spring Data JPA**
- **Thymeleaf** (Template Engine)
- **PostgreSQL** (Production)
- **H2 Database** (Development)
- **Bootstrap 5** (Frontend)
- **Google Fonts** (Playfair Display, Inter)
- **Font Awesome** (Icons)

## License

MIT License
