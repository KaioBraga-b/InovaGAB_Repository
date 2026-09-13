import datetime

# Configurações do Documento
TITLE = "InovaGAB - Plataforma de Inovação Corporativa"
SUBTITLE = "Documentação Oficial de Produto e Arquitetura"
DATE = datetime.datetime.now().strftime("%d de Maio, %Y")
AUTHOR = "Equipe de Desenvolvimento InovaGAB / Gemini CLI"

html_content = f"""
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{TITLE}</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap');
        
        :root {{
            --primary: #1E3A8A;
            --secondary: #3B82F6;
            --accent: #EF4444;
            --bg: #F8F9FD;
            --text: #1F2937;
            --light-text: #6B7280;
            --white: #FFFFFF;
        }}

        body {{
            font-family: 'Inter', sans-serif;
            line-height: 1.6;
            color: var(--text);
            margin: 0;
            padding: 0;
            background: var(--white);
        }}

        .page {{
            padding: 80px;
            max-width: 900px;
            margin: auto;
            min-height: 100vh;
            box-sizing: border-box;
            background: var(--white);
        }}

        .cover {{
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
            color: var(--white);
            height: 100vh;
        }}

        .cover h1 {{
            font-size: 3.5rem;
            margin-bottom: 10px;
            font-weight: 700;
        }}

        .cover h2 {{
            font-size: 1.5rem;
            font-weight: 300;
            opacity: 0.9;
        }}

        .cover .meta {{
            margin-top: 50px;
            font-size: 1.1rem;
        }}

        h1, h2, h3 {{
            color: var(--primary);
        }}

        h2 {{
            border-bottom: 2px solid var(--secondary);
            padding-bottom: 10px;
            margin-top: 40px;
            font-size: 2rem;
        }}

        .section {{
            margin-bottom: 40px;
        }}

        .grid {{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-top: 20px;
        }}

        .card {{
            background: var(--bg);
            padding: 20px;
            border-radius: 12px;
            border-left: 5px solid var(--secondary);
        }}

        .card h4 {{
            margin-top: 0;
            color: var(--primary);
        }}

        .mockup {{
            background: #E5E7EB;
            border-radius: 20px;
            padding: 20px;
            margin: 20px 0;
            text-align: center;
            border: 2px dashed #9CA3AF;
            min-height: 200px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: var(--light-text);
        }}

        .mockup i {{
            font-size: 0.9rem;
            margin-top: 10px;
        }}

        .role-badge {{
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: Bold;
            display: inline-block;
            margin-bottom: 10px;
        }}

        .gestor {{ background: #DCFCE7; color: #16A34A; }}
        .lider {{ background: #F5F3FF; color: #8B5CF6; }}
        .operador {{ background: #EFF6FF; color: #2563EB; }}

        .screenshot-container {{
            border: 1px solid #D1D5DB;
            border-radius: 12px;
            overflow: hidden;
            margin: 30px 0;
            background: #fdfdfd;
        }}

        .screenshot-header {{
            background: #F3F4F6;
            padding: 10px 20px;
            font-weight: bold;
            font-size: 0.9rem;
            border-bottom: 1px solid #D1D5DB;
            display: flex;
            justify-content: space-between;
        }}

        .screenshot-content {{
            padding: 20px;
        }}

        footer {{
            text-align: center;
            padding: 40px;
            color: var(--light-text);
            font-size: 0.9rem;
            border-top: 1px solid #E5E7EB;
        }}

        @media print {{
            .cover {{ height: 100vh; }}
            .page {{ page-break-after: always; }}
        }}
    </style>
</head>
<body>

    <div class="cover">
        <h1>InovaGAB</h1>
        <h2>{SUBTITLE}</h2>
        <div class="meta">
            <p>Data: {DATE}</p>
            <p>Responsável: {AUTHOR}</p>
        </div>
    </div>

    <div class="page">
        <section class="section">
            <h2>1. Visão Geral do Projeto</h2>
            <p>O <strong>InovaGAB</strong> é uma solução digital estratégica desenvolvida para o <strong>Grupo Águia Branca</strong>, focada em democratizar a inovação em todos os níveis da organização. A plataforma permite que desde o colaborador operacional até a alta diretoria participem ativamente da construção do futuro da empresa.</p>
            
            <div class="grid">
                <div class="card">
                    <h4>Objetivo Central</h4>
                    <p>Capturar, gerenciar e executar ideias que gerem eficiência operacional, redução de custos e novos modelos de negócio.</p>
                </div>
                <div class="card">
                    <h4>Diferencial Tecnológico</h4>
                    <p>Integração em tempo real via Firebase, garantindo que mudanças em projetos e estratégias sejam vistas instantaneamente por todos.</p>
                </div>
            </div>
        </section>

        <section class="section">
            <h2>2. Arquitetura e Segurança</h2>
            <p>O aplicativo utiliza uma arquitetura moderna e escalável, baseada nos padrões recomendados pelo Google para desenvolvimento Android.</p>
            <ul>
                <li><strong>UI:</strong> Jetpack Compose (Desenvolvimento declarativo).</li>
                <li><strong>Lógica:</strong> MVVM (Model-View-ViewModel) para separação de preocupações.</li>
                <li><strong>Backend:</strong> Firebase Firestore (NoSQL) e Firebase Auth.</li>
                <li><strong>Sincronização:</strong> Real-time Snapshot Listeners.</li>
            </ul>

            <h3>Controle de Acesso (RBAC)</h3>
            <p>O sistema implementa o <strong>Role-Based Access Control</strong>, onde cada usuário possui um papel definido que dita suas permissões:</p>
            <div class="grid">
                <div class="card">
                    <span class="role-badge operador">OPERADOR</span>
                    <p>Foco na linha de frente. Pode enviar ideias e acompanhar seu progresso.</p>
                </div>
                <div class="card">
                    <span class="role-badge gestor">GESTOR</span>
                    <p>Foco na curadoria e execução. Aprova ideias e gerencia projetos da sua área.</p>
                </div>
                <div class="card">
                    <span class="role-badge lider">LÍDER</span>
                    <p>Foco estratégico e resultados. Define metas do grupo e acompanha o ROI.</p>
                </div>
            </div>
        </section>
    </div>

    <div class="page">
        <section class="section">
            <h2>3. Guia de Telas e Funcionalidades</h2>
            <p>Abaixo, detalhamos os principais fluxos do aplicativo com seus respectivos guias visuais.</p>

            <div class="screenshot-container">
                <div class="screenshot-header">
                    <span>Fluxo de Login e Perfil</span>
                    <span class="operador">Segurança</span>
                </div>
                <div class="screenshot-content">
                    <p>O acesso é protegido por e-mail e senha. O usuário <strong>deve selecionar seu perfil</strong> antes de entrar. O sistema valida se o perfil selecionado condiz com o cadastrado no banco.</p>
                    <div class="mockup">
                        [Screenshot: Tela de Login com seletor de Perfil]
                        <i>Sugestão: Capture a tela de login mostrando os botões de Operador, Gestor e Líder.</i>
                    </div>
                </div>
            </div>

            <div class="screenshot-container">
                <div class="screenshot-header">
                    <span>Dashboard do Operador</span>
                    <span class="operador">Operacional</span>
                </div>
                <div class="screenshot-content">
                    <p>O operador visualiza seu "Impacto" (quantas ideias enviou) e tem acesso rápido para registrar novas sugestões.</p>
                    <div class="mockup">
                        [Screenshot: Home do Operador]
                        <i>Sugestão: Capture o card de "Meu Impacto" e os botões de ação rápida.</i>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <div class="page">
        <section class="section">
            <h2>4. Curadoria e Gestão (Gestor)</h2>
            <p>A tela de curadoria é o coração do processo de triagem das ideias.</p>
            
            <div class="screenshot-container">
                <div class="screenshot-header">
                    <span>Tela de Curadoria</span>
                    <span class="gestor">Gestão</span>
                </div>
                <div class="screenshot-content">
                    <p>O gestor visualiza as ideias pendentes e pode tomar ações imediatas. O sistema bloqueia ações em ideias já processadas para evitar erros.</p>
                    <div class="mockup">
                        [Screenshot: GestorHomeScreen com botões Aprovar/Recusar]
                        <i>Sugestão: Capture o card de uma ideia pendente com as opções de ação ativa.</i>
                    </div>
                </div>
            </div>

            <h2>5. Estratégia e Resultados (Líder)</h2>
            <p>O Líder acompanha os números do grupo e define o norte estratégico.</p>

            <div class="screenshot-container">
                <div class="screenshot-header">
                    <span>Dashboards Estratégicos</span>
                    <span class="lider">Executivo</span>
                </div>
                <div class="screenshot-content">
                    <p>Visualização de ROI, engajamento total e métricas de sucesso de projetos ativos.</p>
                    <div class="mockup">
                        [Screenshot: LiderHomeScreen com gráficos e KPIs]
                        <i>Sugestão: Capture os cards de métricas (ROI Total, Engajamento).</i>
                    </div>
                </div>
            </div>
        </section>

        <section class="section">
            <h2>6. Conclusão</h2>
            <p>O InovaGAB não é apenas um app, é uma ferramenta de transformação cultural que aproxima a base da pirâmide da alta gestão através da tecnologia.</p>
        </section>

        <footer>
            <p>© 2026 Grupo Águia Branca - InovaGAB</p>
            <p>Documento gerado para fins de homologação e treinamento.</p>
        </footer>
    </div>

</body>
</html>
"""

with open("DOCUMENTACAO_INOVAGAB.html", "w", encoding="utf-8") as f:
    f.write(html_content)

print("Documentação gerada com sucesso: DOCUMENTACAO_INOVAGAB.html")
