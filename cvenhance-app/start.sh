set -e

echo "Starting CVEnhance Microservices..."

java -jar /app/auth-service.jar --server.port=8081 &
echo "Auth Service starting on port 8081"

java -jar /app/payment-service.jar --server.port=8082 &
echo "Payment Service starting on port 8082"

java -jar /app/subscription-service.jar --server.port=8083 &
echo "Subscription Service starting on port 8083"

java -jar /app/doc-service.jar --server.port=8084 &
echo "Doc Service starting on port 8084"

java -jar /app/ai-agent-service.jar --server.port=8085 &
echo "AI Agent Service starting on port 8085"

echo "Waiting for services to start..."
sleep 10

echo "Starting API Gateway on port ${PORT:-8080}"
exec java -jar /app/api-gateway.jar --server.port=${PORT:-8080}
