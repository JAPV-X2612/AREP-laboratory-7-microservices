# AWS Deployment — Live Environment

Live, working deployment of the Twitter-like microservices on AWS.

## Live URLs

| Component | URL |
|---|---|
| Frontend (S3, HTTPS) | https://twitter-frontend-arep-dv.s3.us-east-1.amazonaws.com/index.html |
| API Gateway (REST) | https://r84uoiai71.execute-api.us-east-1.amazonaws.com/prod |

## Architecture

```
Browser (React SPA, HTTPS from S3)
        │
        │  Auth0 Universal Login (PKCE)  ──►  Auth0 (dev-amy0edkg02d0ibm1.us.auth0.com)
        │                                       returns JWT access token
        │
        │  HTTPS + Bearer JWT
        ▼
AWS API Gateway (REST, stage: prod)
  ├── GET  /api/stream  → stream-service (Lambda)  → DynamoDB: Posts (scan)
  ├── GET  /api/posts   → post-service   (Lambda)  → DynamoDB: Posts (scan)
  ├── POST /api/posts   → post-service   (Lambda)  → DynamoDB: Posts (put)   [JWT required]
  └── GET  /api/me      → user-service   (Lambda)  → DynamoDB: Users (get/put)[JWT required]
```

## Resources

### Lambda functions (region: us-east-1, runtime: Java 17, memory: 512 MB)

| Function | Handler | Environment |
|---|---|---|
| `twitter-stream-service` | `edu.eci.arep.handler.StreamHandler` | `POSTS_TABLE=Posts` |
| `twitter-post-service`   | `edu.eci.arep.handler.PostHandler`   | `POSTS_TABLE=Posts`, `AUTH0_ISSUER_URI`, `AUTH0_AUDIENCE` |
| `twitter-user-service`   | `edu.eci.arep.handler.UserHandler`   | `USERS_TABLE=Users`, `AUTH0_ISSUER_URI`, `AUTH0_AUDIENCE` |

### DynamoDB tables (on-demand billing)

| Table | Partition Key |
|---|---|
| `Posts` | `id` (String) |
| `Users` | `auth0Id` (String) |

### Auth0

| Setting | Value |
|---|---|
| Tenant Domain | `dev-amy0edkg02d0ibm1.us.auth0.com` |
| SPA Client ID | `LKfpMzThRnpf5Ecq31RzhTYKsdQBEhNL` |
| API Audience  | `https://twitter-arep/api` |
| Allowed Callback / Logout | `https://twitter-frontend-arep-dv.s3.us-east-1.amazonaws.com/index.html` |
| Allowed Web Origins       | `https://twitter-frontend-arep-dv.s3.us-east-1.amazonaws.com` |

### S3

| Bucket | Region | Access |
|---|---|---|
| `twitter-frontend-arep-dv` | us-east-1 | Public read via bucket policy |

HTTPS is served directly by the S3 REST endpoint (`*.s3.us-east-1.amazonaws.com`) — no CloudFront needed (and not available in AWS Academy accounts).

## Deployment Commands (reproducible)

### 1. Build Lambda JARs

```bash
cd microservices/post-service   && mvn clean package -DskipTests -Dmaven.test.skip=true
cd ../stream-service             && mvn clean package -DskipTests -Dmaven.test.skip=true
cd ../user-service               && mvn clean package -DskipTests -Dmaven.test.skip=true
```

### 2. Create DynamoDB tables

```bash
aws dynamodb create-table --table-name Posts \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST

aws dynamodb create-table --table-name Users \
  --attribute-definitions AttributeName=auth0Id,AttributeType=S \
  --key-schema AttributeName=auth0Id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST
```

### 3. Create Lambdas

```bash
ROLE=arn:aws:iam::<ACCOUNT>:role/LabRole

for SVC in post stream user; do
  aws lambda create-function \
    --function-name twitter-${SVC}-service \
    --runtime java17 \
    --handler edu.eci.arep.handler.${SVC^}Handler \
    --role "$ROLE" \
    --zip-file fileb://microservices/${SVC}-service/target/${SVC}-service-1.0-SNAPSHOT.jar \
    --timeout 30 --memory-size 512
done
```

### 4. Configure environment variables

```bash
aws lambda update-function-configuration --function-name twitter-post-service \
  --environment 'Variables={POSTS_TABLE=Posts,AUTH0_ISSUER_URI=https://dev-amy0edkg02d0ibm1.us.auth0.com/,AUTH0_AUDIENCE=https://twitter-arep/api}'

aws lambda update-function-configuration --function-name twitter-stream-service \
  --environment 'Variables={POSTS_TABLE=Posts}'

aws lambda update-function-configuration --function-name twitter-user-service \
  --environment 'Variables={USERS_TABLE=Users,AUTH0_ISSUER_URI=https://dev-amy0edkg02d0ibm1.us.auth0.com/,AUTH0_AUDIENCE=https://twitter-arep/api}'
```

### 5. Create API Gateway

Create a REST API with three child resources under `/api` (`posts`, `stream`, `me`). Each method uses `AWS_PROXY` integration to its Lambda. Enable CORS with `OPTIONS` mock integration on each resource and deploy to stage `prod`.

### 6. Deploy frontend

```bash
cd frontend
cat > .env <<EOF
VITE_AUTH0_DOMAIN=dev-amy0edkg02d0ibm1.us.auth0.com
VITE_AUTH0_CLIENT_ID=LKfpMzThRnpf5Ecq31RzhTYKsdQBEhNL
VITE_AUTH0_AUDIENCE=https://twitter-arep/api
VITE_API_BASE_URL=https://r84uoiai71.execute-api.us-east-1.amazonaws.com/prod
EOF
npm install
npm run build

aws s3 mb s3://twitter-frontend-arep-dv
aws s3api put-public-access-block --bucket twitter-frontend-arep-dv \
  --public-access-block-configuration "BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false"
aws s3api put-bucket-policy --bucket twitter-frontend-arep-dv \
  --policy '{"Version":"2012-10-17","Statement":[{"Sid":"PublicReadGetObject","Effect":"Allow","Principal":"*","Action":"s3:GetObject","Resource":"arn:aws:s3:::twitter-frontend-arep-dv/*"}]}'
aws s3 sync dist/ s3://twitter-frontend-arep-dv/ --delete
```

## Verified Endpoints

```bash
API=https://r84uoiai71.execute-api.us-east-1.amazonaws.com/prod

curl -s "$API/api/stream"                             # → {"posts":[]}
curl -s "$API/api/posts"                              # → []
curl -s -w "%{http_code}\n" "$API/api/me"             # → 401 Missing Authorization header
curl -s -X OPTIONS -D - "$API/api/stream" | grep -i access-control
# → Access-Control-Allow-Origin: *
# → Access-Control-Allow-Methods: GET,POST,OPTIONS
```
