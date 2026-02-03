#!/bin/bash

# 🔐 Script de Configuração Automática de Secrets
# Netflix Mercados API - GitHub Actions Setup

set -e

echo "🚀 Netflix Mercados - GitHub Secrets Setup"
echo "=========================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar gh CLI
if ! command -v gh &> /dev/null; then
    echo -e "${RED}❌ GitHub CLI (gh) não encontrado!${NC}"
    echo "Instale com: brew install gh (macOS) ou sudo apt install gh (Ubuntu)"
    exit 1
fi

# Verificar kubectl
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}❌ kubectl não encontrado!${NC}"
    echo "Instale kubectl: https://kubernetes.io/docs/tasks/tools/"
    exit 1
fi

echo -e "${GREEN}✅ Dependências OK${NC}"
echo ""

# Login GitHub
echo "📝 Verificando autenticação GitHub..."
if ! gh auth status &> /dev/null; then
    echo "Fazendo login no GitHub..."
    gh auth login
fi
echo -e "${GREEN}✅ Autenticado no GitHub${NC}"
echo ""

# Função para gerar kubeconfig base64
generate_kubeconfig() {
    local context=$1
    local output_file=$2
    
    echo "📦 Gerando kubeconfig para contexto: $context"
    
    if ! kubectl config use-context "$context" &> /dev/null; then
        echo -e "${YELLOW}⚠️  Contexto $context não encontrado. Pulando...${NC}"
        return 1
    fi
    
    kubectl config view --flatten --minify > "$output_file"
    cat "$output_file" | base64 -w 0 > "${output_file}.b64"
    
    echo -e "${GREEN}✅ Kubeconfig gerado: ${output_file}.b64${NC}"
    return 0
}

# Diretório temporário
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"

echo "📁 Diretório temporário: $TEMP_DIR"
echo ""

# Menu interativo
echo "🔧 Configuração de Secrets"
echo "=========================="
echo ""
echo "Escolha o que deseja configurar:"
echo ""
echo "1) Todos os secrets (recomendado)"
echo "2) Apenas Kubernetes configs"
echo "3) Apenas secrets opcionais (Slack, Snyk, Codecov)"
echo "4) Configuração manual (passo a passo)"
echo "5) Sair"
echo ""
read -p "Escolha uma opção [1-5]: " option

case $option in
    1)
        echo ""
        echo "🔑 Configurando TODOS os secrets..."
        echo ""
        
        # Kubernetes configs
        echo "📋 KUBERNETES CONFIGS"
        echo "===================="
        echo ""
        
        read -p "Nome do contexto Kubernetes para DEV [default: dev-cluster]: " dev_context
        dev_context=${dev_context:-dev-cluster}
        
        read -p "Nome do contexto Kubernetes para STAGING [default: staging-cluster]: " staging_context
        staging_context=${staging_context:-staging-cluster}
        
        read -p "Nome do contexto Kubernetes para PROD [default: prod-cluster]: " prod_context
        prod_context=${prod_context:-prod-cluster}
        
        # Gerar kubeconfigs
        if generate_kubeconfig "$dev_context" "kubeconfig-dev.yaml"; then
            gh secret set KUBE_CONFIG_DEV < kubeconfig-dev.yaml.b64
            echo -e "${GREEN}✅ KUBE_CONFIG_DEV configurado${NC}"
        fi
        
        if generate_kubeconfig "$staging_context" "kubeconfig-staging.yaml"; then
            gh secret set KUBE_CONFIG_STAGING < kubeconfig-staging.yaml.b64
            echo -e "${GREEN}✅ KUBE_CONFIG_STAGING configurado${NC}"
        fi
        
        if generate_kubeconfig "$prod_context" "kubeconfig-prod.yaml"; then
            gh secret set KUBE_CONFIG_PROD < kubeconfig-prod.yaml.b64
            echo -e "${GREEN}✅ KUBE_CONFIG_PROD configurado${NC}"
        fi
        
        echo ""
        echo "📋 SECRETS OPCIONAIS"
        echo "===================="
        echo ""
        
        # Slack Webhook
        read -p "Slack Webhook URL (ou Enter para pular): " slack_webhook
        if [ -n "$slack_webhook" ]; then
            gh secret set SLACK_WEBHOOK -b "$slack_webhook"
            echo -e "${GREEN}✅ SLACK_WEBHOOK configurado${NC}"
        fi
        
        # Snyk Token
        read -p "Snyk Token (ou Enter para pular): " snyk_token
        if [ -n "$snyk_token" ]; then
            gh secret set SNYK_TOKEN -b "$snyk_token"
            echo -e "${GREEN}✅ SNYK_TOKEN configurado${NC}"
        fi
        
        # Codecov Token
        read -p "Codecov Token (ou Enter para pular): " codecov_token
        if [ -n "$codecov_token" ]; then
            gh secret set CODECOV_TOKEN -b "$codecov_token"
            echo -e "${GREEN}✅ CODECOV_TOKEN configurado${NC}"
        fi
        ;;
        
    2)
        echo ""
        echo "🔑 Configurando Kubernetes configs..."
        echo ""
        
        read -p "Nome do contexto Kubernetes para DEV: " dev_context
        read -p "Nome do contexto Kubernetes para STAGING: " staging_context
        read -p "Nome do contexto Kubernetes para PROD: " prod_context
        
        generate_kubeconfig "$dev_context" "kubeconfig-dev.yaml" && \
            gh secret set KUBE_CONFIG_DEV < kubeconfig-dev.yaml.b64
        
        generate_kubeconfig "$staging_context" "kubeconfig-staging.yaml" && \
            gh secret set KUBE_CONFIG_STAGING < kubeconfig-staging.yaml.b64
        
        generate_kubeconfig "$prod_context" "kubeconfig-prod.yaml" && \
            gh secret set KUBE_CONFIG_PROD < kubeconfig-prod.yaml.b64
        ;;
        
    3)
        echo ""
        echo "🔑 Configurando secrets opcionais..."
        echo ""
        
        read -p "Slack Webhook URL: " slack_webhook
        gh secret set SLACK_WEBHOOK -b "$slack_webhook"
        
        read -p "Snyk Token: " snyk_token
        gh secret set SNYK_TOKEN -b "$snyk_token"
        
        read -p "Codecov Token (ou Enter para pular): " codecov_token
        if [ -n "$codecov_token" ]; then
            gh secret set CODECOV_TOKEN -b "$codecov_token"
        fi
        ;;
        
    4)
        echo ""
        echo "📝 Configuração Manual"
        echo "====================="
        echo ""
        echo "Para configurar manualmente:"
        echo "1. Gere os kubeconfigs:"
        echo "   kubectl config view --flatten --minify | base64 -w 0"
        echo ""
        echo "2. Configure via gh CLI:"
        echo "   gh secret set KUBE_CONFIG_DEV -b '<base64>'"
        echo ""
        echo "3. Ou via web:"
        echo "   https://github.com/gabrielcapiotti/ProjetoMercadoNetflix-Docs/settings/secrets/actions"
        echo ""
        exit 0
        ;;
        
    5)
        echo "Saindo..."
        exit 0
        ;;
        
    *)
        echo -e "${RED}❌ Opção inválida${NC}"
        exit 1
        ;;
esac

echo ""
echo "📊 Verificando secrets configurados..."
echo ""
gh secret list

echo ""
echo -e "${GREEN}✅ Configuração concluída!${NC}"
echo ""
echo "🎯 Próximos passos:"
echo "1. Testar o pipeline:"
echo "   gh workflow run ci-cd.yml"
echo ""
echo "2. Verificar status:"
echo "   gh run list"
echo ""
echo "3. Ver logs:"
echo "   gh run view --log"
echo ""

# Limpar arquivos temporários
echo "🧹 Limpando arquivos temporários..."
rm -rf "$TEMP_DIR"
echo -e "${GREEN}✅ Limpeza concluída${NC}"
