#!/bin/bash

# 🧪 Script de Teste Rápido da API - Netflix Mercados
# Testa todos os 18 endpoints usando curl

set -e

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuração
BASE_URL="${BASE_URL:-http://localhost:8080}"
EMAIL="${AUTH_EMAIL:-admin@netflix.com}"
PASSWORD="${AUTH_PASSWORD:-admin123}"

echo -e "${BLUE}🚀 Netflix Mercados API - Quick Test${NC}"
echo "========================================"
echo ""
echo "Base URL: $BASE_URL"
echo ""

# Contador de sucessos
PASSED=0
FAILED=0

# Função para fazer request e validar
test_endpoint() {
    local name=$1
    local method=$2
    local endpoint=$3
    local data=$4
    local auth=$5
    
    echo -n "Testing: $name ... "
    
    if [ "$auth" = "true" ]; then
        headers="-H 'Authorization: Bearer $ACCESS_TOKEN'"
    else
        headers=""
    fi
    
    if [ "$method" = "GET" ]; then
        response=$(eval curl -s -w "\n%{http_code}" $headers "$BASE_URL$endpoint")
    else
        response=$(eval curl -s -w "\n%{http_code}" -X $method $headers -H "'Content-Type: application/json'" -d "'$data'" "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ PASSED${NC} ($http_code)"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}❌ FAILED${NC} ($http_code)"
        echo "   Response: $body"
        ((FAILED++))
        return 1
    fi
}

# 1. Health Check
echo -e "${YELLOW}📊 Health Check${NC}"
test_endpoint "Health Check" "GET" "/actuator/health" "" "false"
echo ""

# 2. Login
echo -e "${YELLOW}🔐 Authentication${NC}"
echo -n "Login ... "
response=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

ACCESS_TOKEN=$(echo $response | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)

if [ -n "$ACCESS_TOKEN" ]; then
    echo -e "${GREEN}✅ PASSED${NC}"
    echo "   Token: ${ACCESS_TOKEN:0:20}..."
    ((PASSED++))
else
    echo -e "${RED}❌ FAILED${NC}"
    echo "   Response: $response"
    ((FAILED++))
    echo ""
    echo -e "${RED}Cannot proceed without authentication. Exiting.${NC}"
    exit 1
fi
echo ""

# 3. Promoções
echo -e "${YELLOW}🎁 Promoções (2 endpoints)${NC}"
test_endpoint "Aplicar Promoção" "POST" "/api/aplicacao-promocao/aplicar/1" \
    '{"codigoPromocao":"PROMO2024","produtos":[1,2,3]}' "true"

test_endpoint "Reverter Promoção" "POST" "/api/aplicacao-promocao/reverter/1" \
    '{"promocaoId":123}' "true"
echo ""

# 4. Recomendações
echo -e "${YELLOW}💡 Recomendações (3 endpoints)${NC}"
test_endpoint "Recomendações por Mercado" "GET" "/api/recomendacoes/mercado/1?limite=10" "" "true"
test_endpoint "Recomendações por Usuário" "GET" "/api/recomendacoes/usuario/1?limite=10" "" "true"
test_endpoint "Recomendações Populares" "GET" "/api/recomendacoes/populares?limite=10" "" "true"
echo ""

# 5. Relatórios
echo -e "${YELLOW}📊 Relatórios (5 endpoints)${NC}"
QUERY="?dataInicio=2024-01-01&dataFim=2024-12-31"
test_endpoint "Relatório de Vendas" "GET" "/api/relatorios/vendas/1$QUERY" "" "true"
test_endpoint "Relatório de Estoque" "GET" "/api/relatorios/estoque/1$QUERY" "" "true"
test_endpoint "Relatório de Desempenho" "GET" "/api/relatorios/desempenho/1$QUERY" "" "true"
test_endpoint "Relatório de Clientes" "GET" "/api/relatorios/clientes/1$QUERY" "" "true"
test_endpoint "Relatório Financeiro" "GET" "/api/relatorios/financeiro/1$QUERY" "" "true"
echo ""

# 6. Tendências
echo -e "${YELLOW}📈 Tendências (5 endpoints)${NC}"
test_endpoint "Produtos em Alta" "GET" "/api/tendencias/produtos-alta?limite=10" "" "false"
test_endpoint "Mercados em Destaque" "GET" "/api/tendencias/mercados-destaque?limite=10" "" "false"
test_endpoint "Categorias em Crescimento" "GET" "/api/tendencias/categorias-crescimento?limite=10" "" "false"
test_endpoint "Ranking de Vendedores" "GET" "/api/tendencias/ranking-vendedores?limite=10" "" "false"
test_endpoint "Previsão de Demanda" "GET" "/api/tendencias/previsao-demanda?categoria=Eletrônicos&periodo=30" "" "false"
echo ""

# 7. Validação
echo -e "${YELLOW}✅ Validação (3 endpoints)${NC}"
test_endpoint "Validar Email" "POST" "/api/validacao/email" \
    '{"email":"teste@example.com"}' "false"

test_endpoint "Validar URL" "POST" "/api/validacao/url" \
    '{"url":"https://example.com"}' "false"

test_endpoint "Validar Texto Ofensivo" "POST" "/api/validacao/texto-ofensivo" \
    '{"texto":"Texto limpo"}' "false"
echo ""

# Resumo
echo "========================================"
echo -e "${BLUE}📊 Resumo dos Testes${NC}"
echo "========================================"
TOTAL=$((PASSED + FAILED))
SUCCESS_RATE=$(awk "BEGIN {printf \"%.1f\", ($PASSED/$TOTAL)*100}")

echo -e "Total:   $TOTAL endpoints"
echo -e "${GREEN}Passed:  $PASSED ✅${NC}"
echo -e "${RED}Failed:  $FAILED ❌${NC}"
echo -e "Success: ${SUCCESS_RATE}%"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some tests failed. Check the API logs:${NC}"
    echo "   docker-compose logs -f api"
    exit 1
fi
