#!/usr/bin/env python3
"""
Script para remover dependências de Lombok de arquivos Java
"""
import os
import re
from pathlib import Path
from typing import List, Tuple

def get_field_names(content: str) -> List[str]:
    """Extrai nomes de campos privados"""
    pattern = r'private\s+(?:[\w<>\[\],\s]+)\s+(\w+)\s*[;=]'
    matches = re.findall(pattern, content)
    return matches

def generate_no_arg_constructor(class_name: str) -> str:
    """Gera construtor padrão"""
    return f"    public {class_name}() {{\n    }}\n"

def generate_all_arg_constructor(class_name: str, fields: List[str]) -> str:
    """Gera construtor parametrizado"""
    if not fields:
        return ""
    
    # Extrai tipos dos campos
    field_pattern = r'private\s+([\w<>\[\],\s]+)\s+(\w+)\s*[;=]'
    matches = re.findall(field_pattern, content)
    field_map = {name: ftype.strip() for ftype, name in matches}
    
    params = ", ".join([f"{field_map.get(f, 'Object')} {f}" for f in fields])
    assignments = "\n        ".join([f"this.{f} = {f};" for f in fields])
    
    return f"""    public {class_name}({params}) {{
        {assignments}
    }}
"""

def generate_getters_setters(fields: List[str], content: str) -> str:
    """Gera getters e setters"""
    field_pattern = r'private\s+([\w<>\[\],\s]+)\s+(\w+)\s*[;=]'
    matches = re.findall(field_pattern, content)
    field_map = {name: ftype.strip() for ftype, name in matches}
    
    result = ""
    for field in fields:
        ftype = field_map.get(field, 'Object')
        # Capitaliza primeira letra
        field_cap = field[0].upper() + field[1:]
        
        # Getter
        result += f"""    public {ftype} get{field_cap}() {{
        return this.{field};
    }}

"""
        # Setter
        result += f"""    public void set{field_cap}({ftype} {field}) {{
        this.{field} = {field};
    }}

"""
    
    return result

def remove_lombok_from_file(filepath: str) -> bool:
    """Remove Lombok de um arquivo Java"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Se não tem Lombok, pula
        if 'import lombok' not in content:
            return False
        
        original_content = content
        
        # Remove imports de Lombok
        content = re.sub(r'import lombok\..*;\n', '', content)
        content = re.sub(r'import lombok\.extern\..*;\n', '', content)
        
        # Remove anotações Lombok
        content = re.sub(r'@Data\n', '', content)
        content = re.sub(r'@Getter\n', '', content)
        content = re.sub(r'@Setter\n', '', content)
        content = re.sub(r'@NoArgsConstructor\n', '', content)
        content = re.sub(r'@AllArgsConstructor\n', '', content)
        content = re.sub(r'@Builder\n', '', content)
        content = re.sub(r'@Slf4j\n', '', content)
        content = re.sub(r'@RequiredArgsConstructor\n', '', content)
        content = re.sub(r'@RequiredArgsConstructor\(onConstructor_\s*=\s*@Autowired\)\n', '', content)
        
        # Extrai nome da classe
        class_match = re.search(r'public class (\w+)', content)
        if not class_match:
            return False
        
        class_name = class_match.group(1)
        
        # Extrai campos privados
        fields = get_field_names(original_content)
        
        # Encontra a última chave de abertura antes do fechamento da classe
        closing_brace = content.rfind('}')
        if closing_brace == -1:
            return False
        
        # Gera construtores e acessadores
        constructors = generate_no_arg_constructor(class_name)
        
        # Para all-arg constructor, precisa usar o conteúdo original para tipos
        field_pattern = r'private\s+([\w<>\[\],\s]+)\s+(\w+)\s*[;=]'
        matches = re.findall(field_pattern, original_content)
        field_map = {name: ftype.strip() for ftype, name in matches}
        
        if fields:
            params = ", ".join([f"{field_map.get(f, 'Object')} {f}" for f in fields])
            assignments = "\n        ".join([f"this.{f} = {f};" for f in fields])
            constructors += f"""
    public {class_name}({params}) {{
        {assignments}
    }}
"""
        
        # Gera getters e setters
        getters_setters = ""
        for field in fields:
            ftype = field_map.get(field, 'Object')
            field_cap = field[0].upper() + field[1:]
            
            getters_setters += f"""
    public {ftype} get{field_cap}() {{
        return this.{field};
    }}

    public void set{field_cap}({ftype} {field}) {{
        this.{field} = {field};
    }}"""
        
        # Insere construtores antes do fechamento
        if getters_setters or constructors:
            content = content[:closing_brace] + constructors + getters_setters + "\n" + content[closing_brace:]
        
        # Salva o arquivo
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        
        return True
    
    except Exception as e:
        print(f"Erro ao processar {filepath}: {e}")
        return False

def process_directory(directory: str, pattern: str) -> Tuple[int, int]:
    """Processa todos os arquivos Java em um diretório"""
    updated = 0
    skipped = 0
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                if remove_lombok_from_file(filepath):
                    updated += 1
                    print(f"✓ {filepath}")
                else:
                    skipped += 1
    
    return updated, skipped

# Executar para DTOs
print("=" * 80)
print("PROCESSANDO DTOs...")
print("=" * 80)
dto_updated, dto_skipped = process_directory(
    '/workspaces/ProjetoMercadoNetflix-Docs/src/main/java/com/netflix/mercado/dto',
    'dto'
)

# Executar para Entities
print("\n" + "=" * 80)
print("PROCESSANDO ENTITIES...")
print("=" * 80)
entity_updated, entity_skipped = process_directory(
    '/workspaces/ProjetoMercadoNetflix-Docs/src/main/java/com/netflix/mercado/entity',
    'entity'
)

# Executar para Services
print("\n" + "=" * 80)
print("PROCESSANDO SERVICES...")
print("=" * 80)
service_updated, service_skipped = process_directory(
    '/workspaces/ProjetoMercadoNetflix-Docs/src/main/java/com/netflix/mercado/service',
    'service'
)

# Resumo
print("\n" + "=" * 80)
print("RESUMO FINAL")
print("=" * 80)
print(f"DTOs: {dto_updated} atualizados, {dto_skipped} pulados")
print(f"Entities: {entity_updated} atualizados, {entity_skipped} pulados")
print(f"Services: {service_updated} atualizados, {service_skipped} pulados")
print(f"\nTOTAL: {dto_updated + entity_updated + service_updated} arquivos atualizados")
