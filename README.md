# Scale Blog

A modern blog application built with Spring Boot, Thymeleaf, PostgreSQL, and Google OAuth2 authentication.

## Features

- **Landing Page**: Beautiful Medium-style homepage with featured articles
- **Blog Management**: Create, read, and manage blog posts
- **Google OAuth2 Authentication**: Login with Google to create posts
- **User Profiles**: Automatic user creation with Google profile data
- **PostgreSQL Support**: Works with Neon.tech, Supabase, or Scaleway PostgreSQL
- **Responsive Design**: Works on all devices

## Prerequisites

- Java 21+
- PostgreSQL database (local or cloud)
- Google OAuth2 credentials

## Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/heinerbilch/scale.git
cd scale
```

### 2. Configure Environment Variables
```bash
cp .env.example .env
# Edit .env with your configuration
```

### 3. Set up Google OAuth2
1. Go to [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Create a new **OAuth 2.0 Client ID**
3. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
4. Copy **Client ID** and **Client Secret** to your `.env` file

### 4. Set up Database
#### Option A: Neon.tech (Free Tier, Recommended)
1. Sign up at [https://neon.tech](https://neon.tech)
2. Create a new project and database
3. Copy the connection string to `.env`:
   ```
   SPRING_DATASOURCE_URL=postgres://user:password@ep-cool-name-123456.eu-central-1.aws.neon.tech/dbname?sslmode=require
   ```

#### Option B: Local PostgreSQL
```bash
# Install PostgreSQL locally
docker run --name postgres -e POSTGRES_PASSWORD=password -e POSTGRES_USER=postgres -e POSTGRES_DB=scale -p 5432:5432 -d postgres

# Configure .env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/scale
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
```

### 5. Run the Application
```bash
# For development (H2 database)
./gradlew bootRun

# For production (PostgreSQL)
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

## Usage

### Endpoints
| Path | Description | Authentication Required |
|------|-------------|-------------------------|
| `/` | Landing page with featured articles | ❌ No |
| `/blog/posts` | List all blog posts | ❌ No |
| `/blog/posts/{id}` | View a specific post | ❌ No |
| `/blog/posts/new` | Create a new post | ✅ Yes |
| `/login` | Login page | ❌ No |
| `/logout` | Logout | ✅ Yes (if logged in) |

### Creating a Post
1. Click "Anmelden" (Login) on any page
2. Sign in with your Google account
3. Click "Neuen Post erstellen" (Create Post)
4. Fill in title and content
5. Click "Post erstellen" (Create Post)

## Project Structure

```
scale/
├── src/
│   ├── main/
│   │   ├── java/eu/bilch/scale/
│   │   │   ├── config/              # Security & OAuth2 configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── CustomOAuth2UserService.java
│   │   │   ├── controller/          # Web controllers
│   │   │   │   ├── BlogController.java
│   │   │   │   └── AuthController.java
│   │   │   ├── model/               # Entity classes
│   │   │   │   ├── Post.java
│   │   │   │   ├── User.java
│   │   │   │   └── Role.java
│   │   │   ├── repository/          # JPA repositories
│   │   │   │   ├── PostRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   └── ScaleApplication.java
│   │   └── resources/
│   │       ├── templates/          # Thymeleaf templates
│   │       │   ├── auth/            # Authentication templates
│   │       │   │   └── login.html
│   │       │   ├── blog/            # Blog templates
│   │       │   │   ├── create-post.html
│   │       │   │   ├── post.html
│   │       │   │   └── posts.html
│   │       │   └── index.html       # Landing page
│   │       ├── application.yaml     # Configuration
│   │       └── static/              # Static assets
│   └── test/                       # Tests
├── build.gradle.kts                # Build configuration
├── .env.example                    # Environment variables template
└── README.md                       # This file
```

## Configuration Reference

### application.yaml
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid,email,profile
```

### Environment Variables
| Variable | Description | Required |
|----------|-------------|----------|
| `SPRING_DATASOURCE_URL` | JDBC connection URL | ✅ Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | ✅ Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | ✅ Yes |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Client ID | ✅ Yes |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 Client Secret | ✅ Yes |
| `PORT` | Application port (default: 8080) | ❌ No |

## Docker Deployment

### Build the image
```bash
./gradlew bootBuildImage
```

### Run with Docker
```bash
docker run -d \
  --name scale-blog \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://YOUR_DB_HOST:5432/YOUR_DB_NAME \
  -e SPRING_DATASOURCE_USERNAME=YOUR_DB_USERNAME \
  -e SPRING_DATASOURCE_PASSWORD=YOUR_DB_PASSWORD \
  -e GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID \
  -e GOOGLE_CLIENT_SECRET=YOUR_GOOGLE_CLIENT_SECRET \
  -e SPRING_PROFILES_ACTIVE=prod \
  scale:latest
```

## Technologies

- **Spring Boot 4.1.1**
- **Spring Security 6** (OAuth2 Client)
- **Spring Data JPA**
- **Thymeleaf** (Template Engine)
- **PostgreSQL** (Database)
- **H2 Database** (Development)
- **Bootstrap 5** (Frontend)
- **Google Fonts** (Playfair Display, Inter)
- **Font Awesome** (Icons)

## Troubleshooting

### Google OAuth2 not working
1. Check that `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are correct
2. Verify the redirect URI in Google Cloud Console: `http://localhost:8080/login/oauth2/code/google`
3. Ensure you're using `SPRING_PROFILES_ACTIVE=dev` for development

### Database connection issues
1. Verify the connection URL format
2. Check if SSL is required (`?sslmode=require`)
3. Test the connection manually with `psql` or a database client

### H2 Console Access
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:blogdb`
- Username: `sa`
- Password: `tiger`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a pull request

## License

MIT License
