package com.leonida.gtavicompanion.data

import com.leonida.gtavicompanion.model.CharacterProfile
import com.leonida.gtavicompanion.model.CheatCategory
import com.leonida.gtavicompanion.model.CheatCode
import com.leonida.gtavicompanion.model.CheatDirection.DOWN
import com.leonida.gtavicompanion.model.CheatDirection.LEFT
import com.leonida.gtavicompanion.model.CheatDirection.RIGHT
import com.leonida.gtavicompanion.model.CheatDirection.UP
import com.leonida.gtavicompanion.model.ChecklistCategory
import com.leonida.gtavicompanion.model.ChecklistItem
import com.leonida.gtavicompanion.model.FunFact
import com.leonida.gtavicompanion.model.GameplayTip
import com.leonida.gtavicompanion.model.LeonidaRegion
import com.leonida.gtavicompanion.model.SecretCategory
import com.leonida.gtavicompanion.model.SecretLocation
import com.leonida.gtavicompanion.model.StoryChapter

object GtaViCatalog {

    // =========================================================================
    // 1. LUGARES SECRETOS E EASTER EGGS NO MAPA DE LEONIDA
    // Coordenadas normalizadas (mapX: Oeste->Leste 0..1, mapY: Norte->Sul 0..1)
    // =========================================================================
    val secretLocations: List<SecretLocation> = listOf(
        SecretLocation(
            id = "sec_alien_facility",
            title = "Instalação de Pesquisa Alienígena",
            subtitle = "Bunker subterrâneo com senha 2314",
            region = LeonidaRegion.GRASSRIVERS,
            subArea = "Zona Restrita — Sul de Leonida",
            mapX = 0.56f,
            mapY = 0.74f,
            category = SecretCategory.PARANORMAL,
            minChapter = 2,
            timeWindow = "Qualquer horário (melhor à noite)",
            howToFind = "Localize a área cercada restrita no sul de Leonida, encontre o terminal de acesso da comporta e digite o código 2314 no teclado numérico para entrar no bunker governamental com arquivos confidenciais.",
            reward = "Notas de experimentos secretos e linha de evento para reanimar um pequeno alienígena cinza."
        ),
        SecretLocation(
            id = "sec_mile_zero",
            title = "Base Aérea Mile Zero",
            subtitle = "Caças militares e tanques Rhino desde o início",
            region = LeonidaRegion.LEONIDA_KEYS,
            subArea = "Goose Key — Mariana County",
            mapX = 0.44f,
            mapY = 0.88f,
            category = SecretCategory.VEHICLE_MILITARY,
            minChapter = 1,
            timeWindow = "Qualquer horário",
            howToFind = "Siga até Goose Key no extremo sudoeste das Leonida Keys. Invada o perímetro militar pelo acesso leste ou por água com jet ski — esteja preparado para nível de procurado alto imediato!",
            reward = "Acesso antecipado a caças militares e tanques Rhino já a partir do Capítulo 1."
        ),
        SecretLocation(
            id = "sec_dexter_apt",
            title = "Apartamento do Dexter (Trinity)",
            subtitle = "Homenagem fiel à série em Washington Island",
            region = LeonidaRegion.VICE_CITY,
            subArea = "Washington Island — Vice Beach",
            mapX = 0.85f,
            mapY = 0.56f,
            category = SecretCategory.EASTER_EGG,
            minChapter = 1,
            timeWindow = "Dia ou entardecer (melhor visibilidade)",
            howToFind = "Vá até Washington Island em Vice City e procure o condomínio 'Trinity Apartments' (referência ao Trinity Killer). Suba até o telhado e depois desça até a marina vizinha.",
            reward = "Cenário secreto no telhado com caixas de lâminas, lonas e um barco idêntico ao 'Slice of Life' atracado no cais."
        ),
        SecretLocation(
            id = "sec_paul_walker",
            title = "Eclipse Laranja (Velozes & Furiosos)",
            subtitle = "Tributo ao carro clássico em Vespucci Island",
            region = LeonidaRegion.VICE_CITY,
            subArea = "Vespucci Island — Vice-Dale County",
            mapX = 0.77f,
            mapY = 0.62f,
            category = SecretCategory.VEHICLE_MILITARY,
            minChapter = 1,
            timeWindow = "Qualquer horário",
            howToFind = "Explore as mansões residenciais de Vespucci Island e confira a garagem aberta de uma vila costeira voltada para o canal interno.",
            reward = "Cupê esportivo laranja customizado em homenagem direta ao Eclipse 1995 de Brian O'Conner — ótimo carro inicial."
        ),
        SecretLocation(
            id = "sec_skunk_ape",
            title = "O Skunk Ape (Pé-Grande de Leonida)",
            subtitle = "Criptídeo dos pântanos de Cottonmouth Lake",
            region = LeonidaRegion.GRASSRIVERS,
            subArea = "Gullet Marsh / Cottonmouth Lake",
            mapX = 0.43f,
            mapY = 0.64f,
            category = SecretCategory.PARANORMAL,
            minChapter = 1,
            timeWindow = "Dia ou noite (neblina aumenta o clima)",
            howToFind = "Procure os panfletos do Kirk próximos a Gullet Marsh ou escute ruídos pesados entre os manguezais de Cottonmouth Lake. Siga a trilha da criatura até encontrar uma efígie ritualística nas árvores.",
            reward = "Encontro surreal com blecaute e desbloqueio de registro de criptídeo em Grassrivers."
        ),
        SecretLocation(
            id = "sec_kelly_ufo",
            title = "Abdução OVNI de Kelly County",
            subtitle = "Evento alienígena com troféu secreto",
            region = LeonidaRegion.PORT_GELLHORN,
            subArea = "Estrada rural a leste do Starlet Motel",
            mapX = 0.25f,
            mapY = 0.47f,
            category = SecretCategory.PARANORMAL,
            minChapter = 2,
            timeWindow = "Estritamente entre 02:00 e 04:00",
            howToFind = "A partir do Capítulo 2, dirija pelas estradas vicinais a leste de Port Gellhorn (perto do Starlet Motel) após 02:00 da madrugada. Encontre uma viatura abandonada com faróis piscando e portas abertas, e siga a trilha pela mata.",
            reward = "Encontro imediato com raio trator OVNI e conquista/troféu secreto 'The Truth Is Out There'."
        ),
        SecretLocation(
            id = "sec_bud_alien",
            title = "Bud, o Conspiracionista do Céu",
            subtitle = "NPC raro com teorias da Área 69",
            region = LeonidaRegion.PORT_GELLHORN,
            subArea = "Acostamento rural — Kelly County",
            mapX = 0.29f,
            mapY = 0.51f,
            category = SecretCategory.EASTER_EGG,
            minChapter = 2,
            timeWindow = "Ao entardecer (18:00 – 20:30)",
            howToFind = "Descendo a estrada próxima ao local do OVNI em Kelly County ao pôr do sol, procure uma caminhonete estacionada no acostamento com um sujeito olhando para o céu.",
            reward = "Diálogos impagáveis sobre conspirações da Área 69 e uma cerveja gelada direto do cooler dele."
        ),
        SecretLocation(
            id = "sec_vc_portal",
            title = "Portal de Vice City (Tommy Vercetti)",
            subtitle = "Quebra-cabeça das subestações elétricas",
            region = LeonidaRegion.LEONIDA_KEYS,
            subArea = "Início: Torre de Rádio de Key Lento",
            mapX = 0.64f,
            mapY = 0.87f,
            category = SecretCategory.ROCKSTAR_NOD,
            minChapter = 1,
            timeWindow = "Qualquer horário",
            howToFind = "Escale a torre de rádio em Key Lento, ative a unidade de energia e ligue a TV no topo. Siga a sequência nas subestações elétricas interligadas pela rede de alta tensão em Leonida.",
            reward = "Camisa havaiana icônica de Tommy Vercetti, moto customizada retrô e portal visual para a Vice City de 2002!"
        ),
        SecretLocation(
            id = "sec_radio_eggs",
            title = "Ninho de Ovos da Torre de Rádio",
            subtitle = "Escalada de 7 minutos e cena secreta",
            region = LeonidaRegion.VICE_CITY,
            subArea = "Sul de Vice-Dale County",
            mapX = 0.72f,
            mapY = 0.69f,
            category = SecretCategory.EASTER_EGG,
            minChapter = 1,
            timeWindow = "Dia claro (vista panorâmica incrível)",
            howToFind = "Encontre a torre de rádio mais alta ao sul de Vice-Dale County e enfrente a escalada de cerca de 7 minutos até a plataforma final. Inspecione o ninho com ovos pintados e jogue um lá de cima.",
            reward = "Cutscene cômica atingindo um corretor de imóveis lá embaixo + ponto rápido de salto BASE."
        ),
        SecretLocation(
            id = "sec_vault_radio",
            title = "Rádio Secreta 'The Vault'",
            subtitle = "Estação oculta desbloqueada pelo celular",
            region = LeonidaRegion.VICE_CITY,
            subArea = "Sul do clube Jack of Hearts",
            mapX = 0.80f,
            mapY = 0.48f,
            category = SecretCategory.COLLECTIBLE,
            minChapter = 1,
            timeWindow = "Qualquer horário",
            howToFind = "Caminhe pelos becos logo ao sul do Jack of Hearts em Vice City e procure um pôster vermelho com uma máscara de gás na parede. Escaneie o pôster com o aplicativo do celular.",
            reward = "Desbloqueia permanentemente a estação/playlist exclusiva 'The Vault' no rádio dos veículos."
        ),
        SecretLocation(
            id = "sec_arthur_hat",
            title = "Chapéu do Arthur Morgan (RDR2)",
            subtitle = "Relíquia de Red Dead em Port Gellhorn",
            region = LeonidaRegion.PORT_GELLHORN,
            subArea = "Bairro residencial — Sul de Port Gellhorn",
            mapX = 0.21f,
            mapY = 0.56f,
            category = SecretCategory.ROCKSTAR_NOD,
            minChapter = 2,
            timeWindow = "Horário comercial (dia)",
            howToFind = "Procure uma venda de garagem (yard sale) na zona sul de Port Gellhorn. Observe com atenção um sofá velho com um case de violão apoiado em cima.",
            reward = "O lendário chapéu de cowboy do Arthur Morgan de RDR2 em exposição como easter egg nostálgico."
        ),
        SecretLocation(
            id = "sec_cool_dog",
            title = "Cachorro do Jet Ski ('Cool Dog')",
            subtitle = "Resgate marítimo até a Ilha dos Cachorros",
            region = LeonidaRegion.LEONIDA_KEYS,
            subArea = "Águas abertas — Leste das Keys",
            mapX = 0.82f,
            mapY = 0.83f,
            category = SecretCategory.EASTER_EGG,
            minChapter = 1,
            timeWindow = "Dia claro em mar aberto",
            howToFind = "Afaste-se bastante da costa até ficar à deriva sem barco em águas abertas e aguarde escutar latidos próximos. Um cachorro de óculos escuros chegará pilotando um jet ski!",
            reward = "Ele resgata você e o guia até uma ilha secreta de cachorros com um belo maço de dinheiro escondido."
        ),
        SecretLocation(
            id = "sec_meteorite",
            title = "Cratera do Meteorito",
            subtitle = "Fragmento espacial brilhante em Mount Kalaga",
            region = LeonidaRegion.MOUNT_KALAGA,
            subArea = "Parque Nacional Mount Kalaga — Lummox",
            mapX = 0.50f,
            mapY = 0.14f,
            category = SecretCategory.COLLECTIBLE,
            minChapter = 1,
            timeWindow = "À noite (brilha forte na escuridão)",
            howToFind = "Explore as trilhas altas do Parque Nacional Mount Kalaga ao norte do mapa durante a noite. Procure o brilho esverdeado/incandescente entre as árvores em uma pequena depressão rochosa.",
            reward = "Fragmento raro de meteorito para sua coleção de descobertas."
        ),
        SecretLocation(
            id = "sec_michaels_boat",
            title = "O Barco Perdido do Michael (GTA V)",
            subtitle = "GPS ainda marca Los Santos!",
            region = LeonidaRegion.LEONIDA_KEYS,
            subArea = "Cais próximo à casa inicial do Jason — Key Lento",
            mapX = 0.57f,
            mapY = 0.84f,
            category = SecretCategory.ROCKSTAR_NOD,
            minChapter = 1,
            timeWindow = "Qualquer horário",
            howToFind = "Logo no começo do jogo, inspecione o barco atracado ao lado da casa inicial do Jason em Key Lento. Entre na cabine em primeira pessoa e olhe o painel do GPS.",
            reward = "Confirmação histórica de que é o barco roubado de Michael De Santa em GTA V — o GPS ainda registra Los Santos!"
        ),
        SecretLocation(
            id = "sec_ambrosia_refinery",
            title = "Silo Secreto dos Canaviais",
            subtitle = "Ponto de salto e estoque escondido em Ambrosia",
            region = LeonidaRegion.AMBROSIA,
            subArea = "Noroeste do Lago Leonida — Leonard County",
            mapX = 0.51f,
            mapY = 0.36f,
            category = SecretCategory.COLLECTIBLE,
            minChapter = 1,
            timeWindow = "Qualquer horário",
            howToFind = "Atravesse os campos de cana a noroeste do Lago Leonida até a antiga refinaria de açúcar. Suba pela passarela enferrujada atrás dos tanques de melaço.",
            reward = "Equipamentos táticos, dinheiro vivo e vista privilegiada para observar fauna com binóculos."
        )
    )

    // =========================================================================
    // 2. CHEAT CODES DE GTA VI (SISTEMA OFICIAL DE 61 CHEATS)
    // =========================================================================
    val cheats: List<CheatCode> = listOf(
        // PLAYER
        CheatCode(
            id = "cheat_cash_250k",
            title = "US$ 250.000 em Dinheiro",
            description = "Adiciona imediatamente US$ 250.000 na conta de Jason ou Lucia.",
            category = CheatCategory.PLAYER,
            sequence = listOf(UP, RIGHT, DOWN, LEFT, UP, LEFT, DOWN, RIGHT),
            unlockRequirement = "Concluir a história principal (91 missões).",
            extraNote = "Liberado automaticamente assim que os créditos sobem."
        ),
        CheatCode(
            id = "cheat_unlimited_focus",
            title = "Modo Foco Ilimitado",
            description = "Mantém a barra da habilidade especial de foco sempre cheia durante tiroteios e fugas.",
            category = CheatCategory.PLAYER,
            sequence = listOf(DOWN, UP, UP, DOWN, UP, DOWN, UP, DOWN, UP, RIGHT),
            unlockRequirement = "Concluir a história principal."
        ),
        CheatCode(
            id = "cheat_body_armor",
            title = "Colete Balístico Máximo",
            description = "Preenche 100% da barra de proteção balística instantaneamente.",
            category = CheatCategory.PLAYER,
            sequence = listOf(DOWN, UP, LEFT, UP, UP, UP, UP, UP, DOWN, DOWN),
            unlockRequirement = "Completar 100 treinos na academia / áreas fitness."
        ),
        CheatCode(
            id = "cheat_drunk_mode",
            title = "Modo Bêbado",
            description = "Deixa o personagem cambaleando com câmera oscilante e reações hilárias.",
            category = CheatCategory.PLAYER,
            sequence = listOf(UP, LEFT, RIGHT, UP, UP, LEFT, RIGHT, UP, LEFT, RIGHT),
            unlockRequirement = "Gastar US$ 2.500 em bebidas alcoólicas em bares e clubes."
        ),
        CheatCode(
            id = "cheat_raise_heat",
            title = "Aumentar Pressão Policial (3+ Estrelas)",
            description = "Eleva imediatamente o nível de calor/perseguição policial para testar fugas.",
            category = CheatCategory.PLAYER,
            sequence = listOf(LEFT, DOWN, RIGHT, DOWN, UP, DOWN, UP),
            unlockRequirement = "Escapar de um nível de procurado de 3 estrelas 25 vezes."
        ),
        CheatCode(
            id = "cheat_raise_wanted",
            title = "Aumentar Nível de Procurado Máximo",
            description = "Dispara alerta pesado das forças policiais de Leonida instantaneamente.",
            category = CheatCategory.PLAYER,
            sequence = listOf(LEFT, DOWN, RIGHT, DOWN, UP, RIGHT, UP),
            unlockRequirement = "Escapar de um nível de procurado de 3 estrelas 50 vezes."
        ),
        CheatCode(
            id = "cheat_no_fall",
            title = "Sem Dano de Queda",
            description = "Permite pular de prédios em Vice City ou penhascos de Mount Kalaga sem morrer.",
            category = CheatCategory.PLAYER,
            sequence = null,
            unlockRequirement = "Sobreviver a 20 quedas livres de mais de 12 metros (40 pés).",
            extraNote = "Consulte a sequência gerada em Progresso > Cheats após cumprir o desafio."
        ),
        CheatCode(
            id = "cheat_parachute",
            title = "Equipar Paraquedas",
            description = "Adiciona um paraquedas pronto para uso imediato no inventário.",
            category = CheatCategory.PLAYER,
            sequence = null,
            unlockRequirement = "Completar 50 saltos BASE espalhados por Leonida.",
            extraNote = "Sequência exibida em Progresso > Cheats ao desbloquear."
        ),

        // COMBAT
        CheatCode(
            id = "cheat_max_ammo",
            title = "Munição Máxima",
            description = "Enche o pente e a reserva de todas as armas atualmente equipadas.",
            category = CheatCategory.COMBAT,
            sequence = listOf(DOWN, UP, RIGHT, DOWN, LEFT, LEFT, UP, UP, DOWN, LEFT),
            unlockRequirement = "Conquistar 25 medalhas de Bronze ou superior nos estandes de tiro."
        ),
        CheatCode(
            id = "cheat_infinite_ammo",
            title = "Munição Infinita",
            description = "Suas armas nunca ficam sem balas na reserva.",
            category = CheatCategory.COMBAT,
            sequence = listOf(DOWN, UP, RIGHT, DOWN, LEFT, LEFT, UP, DOWN, DOWN, DOWN),
            unlockRequirement = "Conquistar 25 medalhas de Prata ou superior nos estandes de tiro."
        ),
        CheatCode(
            id = "cheat_no_reload",
            title = "Sem Recarregamento",
            description = "Disparo contínuo sem jamais precisar trocar de pente.",
            category = CheatCategory.COMBAT,
            sequence = listOf(DOWN, UP, RIGHT, DOWN, LEFT, LEFT, UP, DOWN, DOWN, UP),
            unlockRequirement = "Conquistar 25 medalhas de Ouro nos estandes de tiro."
        ),
        CheatCode(
            id = "cheat_attack_force",
            title = "Força de Impacto (Attack Force)",
            description = "Socos, golpes corpo a corpo e tiros arremessam inimigos longe pelo ar.",
            category = CheatCategory.COMBAT,
            sequence = listOf(DOWN, UP, LEFT, LEFT, RIGHT, DOWN, DOWN, UP, DOWN),
            unlockRequirement = "Concluir a história principal."
        ),
        CheatCode(
            id = "cheat_exploding_balls",
            title = "Bolas Explosivas",
            description = "Bolas esportivas pelo mapa explodem violentamente ao impacto!",
            category = CheatCategory.COMBAT,
            sequence = listOf(DOWN, UP, LEFT, LEFT, RIGHT, DOWN, DOWN, UP, RIGHT),
            unlockRequirement = "Atingir 20 inimigos com dano explosivo."
        ),
        CheatCode(
            id = "cheat_vehicle_force",
            title = "Força de Impacto Veicular",
            description = "Colisões com seu veículo lançam carros e obstáculos como brinquedos.",
            category = CheatCategory.COMBAT,
            sequence = listOf(DOWN, UP, LEFT, LEFT, RIGHT, DOWN, DOWN, LEFT, UP, DOWN),
            unlockRequirement = "Eliminar 25 inimigos usando um veículo."
        ),

        // VEHICLES
        CheatCode(
            id = "cheat_boomazoom",
            title = "Spawn: Maibatsu Boomazoom",
            description = "Invoca instantaneamente o veículo Maibatsu Boomazoom à sua frente.",
            category = CheatCategory.VEHICLE,
            sequence = listOf(LEFT, UP, RIGHT, DOWN, LEFT, LEFT, UP, LEFT),
            unlockRequirement = "Completar 5 desafios de compras em Leonida."
        ),
        CheatCode(
            id = "cheat_mtl_pumper",
            title = "Spawn: Caminhão MTL Pumper",
            description = "Invoca o caminhão de bombeiros MTL Pumper pronto para causar caos.",
            category = CheatCategory.VEHICLE,
            sequence = listOf(LEFT, UP, RIGHT, DOWN, LEFT, RIGHT, RIGHT, UP),
            unlockRequirement = "Descobrir o MTL Pumper no menu de Progresso (escaneie via WAiNK)."
        ),
        CheatCode(
            id = "cheat_bulldozer",
            title = "Spawn: HVY Bulldozer",
            description = "Invoca a escavadeira pesada HVY Bulldozer com pá frontal.",
            category = CheatCategory.VEHICLE,
            sequence = listOf(LEFT, UP, RIGHT, DOWN, LEFT, RIGHT, RIGHT, DOWN),
            unlockRequirement = "Descobrir a HVY Bulldozer no menu de Progresso (escaneie via WAiNK)."
        ),
        CheatCode(
            id = "cheat_rhino_tank",
            title = "Spawn: Tanque Rhino & Veículos Especiais",
            description = "Inclui Rhino Tank, Maibatsu Sanchez, Sea Sparrow, Speeder 700 GT e Truffade Thrax.",
            category = CheatCategory.VEHICLE,
            sequence = null,
            unlockRequirement = "Vencer etapas em Gellhorn Raceway, Thrillbilly Mud Club, Boone Speedway e desafios de descoberta.",
            extraNote = "Cada veículo libera seu código individual em Progresso > Cheats."
        ),

        // ENVIRONMENT
        CheatCode(
            id = "cheat_cycle_weather",
            title = "Alternar Clima de Leonida",
            description = "Alterna entre sol tropical, tempestade de raios, neblina pantaneira e céu limpo.",
            category = CheatCategory.ENVIRONMENT,
            sequence = listOf(DOWN, RIGHT, UP, DOWN, RIGHT, UP, RIGHT),
            unlockRequirement = "Concluir a história principal / revelar o mapa de Leonida."
        ),
        CheatCode(
            id = "cheat_time_control",
            title = "Avançar / Pausar Hora do Dia",
            description = "Avança o relógio rapidamente ou congela o pôr do sol de Vice City para fotos.",
            category = CheatCategory.ENVIRONMENT,
            sequence = null,
            unlockRequirement = "Coletar relógios vintage escondidos pelo estado de Leonida.",
            extraNote = "Ótimo para forçar o horário 02:00 – 04:00 em caçadas pós-game."
        ),

        // FILTERS
        CheatCode(
            id = "cheat_cataclysm",
            title = "Filtro: Cataclismo",
            description = "Cobre Leonida com uma atmosfera densa de fumaça sombria e apocalíptica.",
            category = CheatCategory.FILTER,
            sequence = listOf(DOWN, DOWN, UP, RIGHT, RIGHT, UP, LEFT, LEFT, RIGHT, RIGHT),
            unlockRequirement = "Descobrir o carro 'Martha's Vamos' no menu de Progresso."
        ),
        CheatCode(
            id = "cheat_videotape",
            title = "Filtro: Fita VHS (Videotape)",
            description = "Transforma o visual do jogo em gravação analógica retrô anos 80/90.",
            category = CheatCategory.FILTER,
            sequence = listOf(DOWN, DOWN, UP, RIGHT, RIGHT, UP, LEFT, LEFT, RIGHT, DOWN),
            unlockRequirement = "Completar 50 atividades junto com seu parceiro (Jason & Lucia)."
        ),
        CheatCode(
            id = "cheat_faded_glory",
            title = "Filtro: Faded Glory (TV de Tubo CRT)",
            description = "Aplica linhas de varredura e cores nostálgicas de televisão CRT clássica.",
            category = CheatCategory.FILTER,
            sequence = null,
            unlockRequirement = "Assaltar 50 lojas em dupla com seu parceiro.",
            extraNote = "Um dos 16 filtros visuais secretos desbloqueáveis no pós-game."
        )
    )

    // =========================================================================
    // 3. RESUMO DOS CAPÍTULOS (100% ANTI-SPOILER DE CAPÍTULOS POSTERIORES!)
    // =========================================================================
    val chapters: List<StoryChapter> = listOf(
        StoryChapter(
            number = 1,
            title = "Just Like Paradise",
            subtitle = "As raízes nas Leonida Keys",
            region = LeonidaRegion.LEONIDA_KEYS,
            approxMissions = 22,
            safeEntryPremise = "A jornada começa no arquipélago ensolarado das Leonida Keys. Você acompanha a rotina de Jason entre pequenos contrabandos, cobranças locais e trabalhos na marina, enquanto a situação de Lucia na penitenciária corre em paralelo até o reencontro dos dois.",
            completedRecap = "Jason estabeleceu sua base em Key Lento trabalhando sob a tutela do veterano Brian Heder e ao lado do amigo conspiracionista Cal Hampton. Com a saída de Lucia, o casal voltou a atuar em sintonia nas ilhas tropicais — mas um golpe mal calculado os colocou na mira errada, forçando uma retirada urgente rumo ao continente.",
            keyCharacters = listOf("Jason Duval", "Lucia Caminos", "Brian Heder", "Cal Hampton"),
            gameplayTips = listOf(
                "Compre um silenciador na Ammu-Nation de Key Lento logo cedo — muda o jogo em furtividade.",
                "Comece a usar o app WAiNK no celular para escanear carros e barcos desde já (meta 100%: 200 modelos).",
                "Visite o cais perto da casa inicial do Jason para ver o easter egg do barco do Michael (GTA V)!"
            )
        ),
        StoryChapter(
            number = 2,
            title = "Paradise Lost",
            subtitle = "Sobrevivência em Port Gellhorn",
            region = LeonidaRegion.PORT_GELLHORN,
            approxMissions = 13,
            safeEntryPremise = "Longe das águas cristalinas das Keys, a dupla busca abrigo na decadente cidade portuária industrial de Port Gellhorn, em Kelly County. Operando a partir de um motel barato, eles precisam se recompor, levantar grana rápida e criar contatos locais.",
            completedRecap = "Hospedados no Starlet Motel, Lucia assumiu a linha de frente em furtos rápidos e pequenos assaltos enquanto Jason se recuperava. Aos poucos, os dois dominaram os esquemas locais da costa do Golfo, acumularam capital e abriram o caminho para finalmente mirar na metrópole.",
            keyCharacters = listOf("Jason Duval", "Lucia Caminos", "Contatos de Kelly County"),
            gameplayTips = listOf(
                "A partir deste capítulo, o evento secreto do OVNI perto do Starlet Motel fica disponível (02:00 – 04:00).",
                "Procure o chapéu do Arthur Morgan em uma venda de garagem no sul de Port Gellhorn.",
                "Aproveite para guardar dinheiro com segurança e gerenciar seu Perfil Criminal."
            )
        ),
        StoryChapter(
            number = 3,
            title = "T*ts, Ass, Drug Money",
            subtitle = "A chegada à selva neon de Vice City",
            region = LeonidaRegion.VICE_CITY,
            approxMissions = 26,
            safeEntryPremise = "Um dos capítulos mais robustos do jogo: Jason e Lucia enfim pisam no epicentro luminoso de Vice City. Novas figuras influentes da cena musical, imobiliária e do submundo entram no radar conforme as operações sobem de escala.",
            completedRecap = "Em Vice City, a dupla entrou na órbita de nomes pesados como Boobie Ike, Dre'Quan Priest e Raul Bautista. Os golpes deixaram de ser improvisos de beira de estrada para envolver redes complexas, grandes quantias e disputas territoriais dentro da metrópole.",
            keyCharacters = listOf("Boobie Ike", "Dre'Quan Priest", "Raul Bautista"),
            gameplayTips = listOf(
                "Neste capítulo são liberados os 7 golpes de Payday Loan — ótimos para a meta de Notas Marcadas.",
                "Explore Washington Island (Apartamento do Dexter) e escaneie o pôster da rádio secreta The Vault.",
                "Use o ciclo dia/noite a seu favor: movimentação policial e público mudam muito à noite."
            )
        ),
        StoryChapter(
            number = 4,
            title = "Vice City, Baby",
            subtitle = "Conspiração no topo da metrópole",
            region = LeonidaRegion.VICE_CITY,
            approxMissions = 25,
            safeEntryPremise = "Consolidados no ecossistema criminoso de Vice-Dale e expandindo o alcance para áreas vizinhas como Leonard County e polos de trânsito, Jason e Lucia encaram operações mais arriscadas enquanto equilibram facções rivais e pressão policial crescente.",
            completedRecap = "A dupla provou seu peso entre os maiores nomes de Leonida, navegando alianças frágeis, grandes alvos urbanos e ramificações que conectam Vice City ao interior do estado. Todas as peças ficaram posicionadas para o confronto decisivo.",
            keyCharacters = listOf("Jason Duval", "Lucia Caminos", "Lideranças de Vice-Dale"),
            gameplayTips = listOf(
                "Ótimo momento para adiantar Operações de Gangues (39) e Assaltos a Bancos (22) enquanto explora a cidade.",
                "Lembre-se de usar os binóculos ao passar por Ambrosia e Mount Kalaga para somar espécies estudadas.",
                "Mantenha o condicionamento físico e armas atualizadas para missões mais intensas."
            )
        ),
        StoryChapter(
            number = 5,
            title = "Top of the World, Ma!",
            subtitle = "O Grande Ato Final",
            region = LeonidaRegion.VICE_CITY,
            approxMissions = 5,
            safeEntryPremise = "A reta final concentrada e intensa da campanha. As linhas narrativas espalhadas por toda Leonida convergem para o momento de maior tensão na jornada de Jason e Lucia — aproveite cada cena sem pressa!",
            completedRecap = "Você concluiu as 91 missões principais da saga de Jason e Lucia em Leonida! Agora o menu oficial de 100% (Progresso > Estatísticas) e o sistema de 61 Cheats estão liberados, e absolutamente nada do checklist de 100% é perdível no pós-game.",
            keyCharacters = listOf("Jason Duval", "Lucia Caminos"),
            gameplayTips = listOf(
                "Fique tranquilo: nenhum requisito do troféu de 100% ('A Long Road') é perdível!",
                "Após os créditos, confira Progresso > Cheats e faça um save manual separado antes de testar qualquer código."
            )
        )
    )

    // =========================================================================
    // 4. CHECKLIST COMPLETO DOS 35 REQUISITOS DO 100% ("A LONG ROAD")
    // =========================================================================
    val completionChecklist: List<ChecklistItem> = listOf(
        // Categoria 1: História & Empreitadas (12)
        ChecklistItem("story_main", "História Principal", "Completar todas as 91 missões da campanha", ChecklistCategory.STORY_TASKS, "Avance nos 5 capítulos sem medo — nada do 100% é perdível."),
        ChecklistItem("story_optional", "Tarefas Opcionais & Estranhos", "Completar pelo menos 100 tarefas opcionais", ChecklistCategory.STORY_TASKS, "Há mais de 200 disponíveis no mapa; faça as que encontrar naturalmente."),
        ChecklistItem("story_gangs", "Operações de Gangues", "Limpar todas as 39 Operações de Gangues", ChecklistCategory.STORY_TASKS, "Marcadas por ícones amarelos com máscara no mapa. Saqueie os itens de valor!"),
        ChecklistItem("story_banks", "Assaltos a Bancos", "Completar todos os 22 Bancos (14 pequenos + 8 cofres grandes)", ChecklistCategory.STORY_TASKS, "Planeje a rota de fuga e o horário para reduzir resposta policial."),
        ChecklistItem("story_payday", "Empréstimos Payday Loan", "Completar todas as 7 operações Payday Loan", ChecklistCategory.STORY_TASKS, "Desbloqueia a partir do Capítulo 3."),
        ChecklistItem("story_smash", "Smash 'n' Grabs", "Completar 34 de 35 roubos (20 joalherias + 15 penhores)", ChecklistCategory.STORY_TASKS, "Não esqueça de abrir os cofres nos fundos de cada loja!"),
        ChecklistItem("story_stickups", "Assaltos Rápidos (Stick Ups)", "Realizar 50 assaltos em conveniências/fast-food (de 91)", ChecklistCategory.STORY_TASKS, "Use o contador rápido na tela inicial para acompanhar seu progresso."),
        ChecklistItem("story_fence_narc", "Receptação: Narcóticos", "Vender US$ 100.000 em narcóticos", ChecklistCategory.STORY_TASKS, "Obtidos em Operações de Gangues e atividades como os flamingos."),
        ChecklistItem("story_fence_jewel", "Receptação: Joias", "Vender US$ 100.000 em joias", ChecklistCategory.STORY_TASKS, "Vem naturalmente dos roubos Smash 'n' Grabs."),
        ChecklistItem("story_fence_bills", "Receptação: Notas Marcadas", "Lavar/vender US$ 250.000 em notas marcadas", ChecklistCategory.STORY_TASKS, "Recompensa principal dos Bancos e Payday Loans."),
        ChecklistItem("story_fence_veh", "Receptação: Veículos", "Vender US$ 200.000 em veículos roubados", ChecklistCategory.STORY_TASKS, "Entregue carros para Moses, Randy ou pontos Pay 'n' Spray."),
        ChecklistItem("story_fence_total", "Marco Total de Receptação", "Acumular US$ 650.000+ em bens receptados", ChecklistCategory.STORY_TASKS, "Fecha automaticamente ao concluir as 4 metas de receptação acima."),

        // Categoria 2: Exploração & Atividades (20)
        ChecklistItem("act_kenton", "Naufrágios de Kenton", "Completar 5 de 7 caças ao tesouro de Kenton", ChecklistCategory.EXPLORATION_ACTIVITIES, "Ótimo para explorar o litoral das Leonida Keys com equipamento de mergulho."),
        ChecklistItem("act_underwater", "Descobertas Subaquáticas", "100% dos naufrágios e baús submarinos", ChecklistCategory.EXPLORATION_ACTIVITIES, "Leve tanque de mergulho e escaneie barcos enquanto navega."),
        ChecklistItem("act_shooting", "Estandes de Tiro (Gun Club)", "Conquistar todas as 42 medalhas de Ouro", ChecklistCategory.EXPLORATION_ACTIVITIES, "Também desbloqueia os cheats de Munição Infinita e Sem Recarregamento!"),
        ChecklistItem("act_drone", "Corridas de Drone", "Vencer todos os circuitos de corrida de drone", ChecklistCategory.EXPLORATION_ACTIVITIES, "Pratique curvas fechadas em primeira pessoa para bater os tempos."),
        ChecklistItem("act_races", "Corridas de Rua, Barco & Off-Road", "Pódio/Ouro nas séries oficiais de corridas", ChecklistCategory.EXPLORATION_ACTIVITIES, "Inclui Gellhorn Raceway, Thrillbilly Mud Club e Boone Speedway."),
        ChecklistItem("act_bret", "Contratos de Caça do Bret", "Concluir todos os 15 alvos do baralho do Bret", ChecklistCategory.EXPLORATION_ACTIVITIES, "Aproveite para observar os animais com binóculos antes de caçar."),
        ChecklistItem("act_pool", "Oponentes de Sinuca", "Derrotar 5 de 7 jogadores de sinuca nos bares", ChecklistCategory.EXPLORATION_ACTIVITIES, "Procure mesas de bilhar em bares de Port Gellhorn e Vice City."),
        ChecklistItem("act_minigolf", "Minigolfe", "Concluir partidas em 2 dos 3 campos de minigolfe", ChecklistCategory.EXPLORATION_ACTIVITIES, "Rápido e relaxante para fazer em dupla com Jason e Lucia."),
        ChecklistItem("act_meditation", "Pontos de Meditação", "Completar sessões nos pontos zen", ChecklistCategory.EXPLORATION_ACTIVITIES, "Dica de ouro: fazer com Jason E com Lucia conta em dobro!"),
        ChecklistItem("act_arcade", "Fliperamas & Passatempos", "Bater pontuações nos gabinetes de arcade", ChecklistCategory.EXPLORATION_ACTIVITIES, "Visite fliperamas retrô espalhados por Vice Beach."),
        ChecklistItem("act_stunts", "Saltos Únicos de Veículo", "Completar a cota de saltos acrobáticos", ChecklistCategory.EXPLORATION_ACTIVITIES, "Motos rápidas facilitam pousos limpos nas rampas estreitas."),
        ChecklistItem("act_maneuvers", "Manobras Especiais Veiculares", "Realizar 300 manobras especiais com sucesso", ChecklistCategory.EXPLORATION_ACTIVITIES, "Drifts, empinadas e saltos contam continuamente no dia a dia."),
        ChecklistItem("act_challenges", "Desafios Gerais", "Concluir 70 dos 80 desafios gerais do jogo", ChecklistCategory.EXPLORATION_ACTIVITIES, "Acompanhe na aba Progresso — muitos completam jogando normalmente."),
        ChecklistItem("act_flight", "Escola de Aviação", "Concluir as provas e pousos aéreos", ChecklistCategory.EXPLORATION_ACTIVITIES, "Treine no aeroporto VCIA antes de tentar acrobacias avançadas."),
        ChecklistItem("act_dealers", "Traficantes de Rua", "Encontrar e negociar com dealers ocultos", ChecklistCategory.EXPLORATION_ACTIVITIES, "Fique atento a becos iluminados em Vice City à noite."),
        ChecklistItem("act_gym", "Marcos de Academia & Fitness", "Atingir metas de força e resistência", ChecklistCategory.EXPLORATION_ACTIVITIES, "100 treinos também liberam o cheat de Colete Máximo!"),
        ChecklistItem("act_bounty", "Contratos de Caça-Recompensas", "Entregar fugitivos para a fiança (Bail Bonds)", ChecklistCategory.EXPLORATION_ACTIVITIES, "Capturar vivo rende bônus extra de dinheiro."),
        ChecklistItem("act_fight", "Lutas Clandestinas", "Vencer os torneios de luta de rua", ChecklistCategory.EXPLORATION_ACTIVITIES, "Treinar na academia antes facilita muito os nocautes."),
        ChecklistItem("act_vigilante", "Atividades de Vigilante", "Resolver ocorrências via scanner policial", ChecklistCategory.EXPLORATION_ACTIVITIES, "Use viatura apreendida para acessar chamados rápidos."),
        ChecklistItem("act_random", "Eventos Aleatórios de Estrada", "Resolver a cota de encontros dinâmicos", ChecklistCategory.EXPLORATION_ACTIVITIES, "Pare sempre que um ponto azul/vermelho pulsar no minimapa."),

        // Categoria 3: Descoberta & Catálogo (3)
        ChecklistItem("disc_waink", "Escaneamento no App WAiNK", "Escanear 200 modelos únicos de veículos (de 277)", ChecklistCategory.DISCOVERY, "Faça disso um hábito desde o Cap. 1: viu carro/barco/avião novo, escaneie!"),
        ChecklistItem("disc_animals", "Estudo de Fauna com Binóculos", "Estudar 100 de 255 espécies de animais", ChecklistCategory.DISCOVERY, "Não precisa caçar nem abater! Apenas observar nos binóculos já registra."),
        ChecklistItem("disc_regions", "Mapa de Leonida & Segredos", "Dissipar a névoa das 6 regiões e coletar segredos", ChecklistCategory.DISCOVERY, "Sobrevoe as áreas finais com avião ou helicóptero para limpar 100% do mapa.")
    )

    // =========================================================================
    // 5. FUN FACTS & CURIOSIDADES VERIFICADAS DE GTA VI
    // =========================================================================
    val funFacts: List<FunFact> = listOf(
        FunFact(
            id = "fact_michael_boat",
            title = "O Barco Perdido do Michael Está Aqui!",
            tag = "Conexão GTA V",
            summary = "O barco estacionado ao lado da casa inicial do Jason em Key Lento é exatamente a embarcação que Michael De Santa perdeu em GTA V. Se entrar nele e olhar o GPS no painel, o último destino registrado ainda aparece como Los Santos!",
            whereToSpot = "Cais ao lado da casa inicial do Jason em Key Lento (Cap. 1).",
            minChapter = 1
        ),
        FunFact(
            id = "fact_race_chase",
            title = "A Rockstar Tirando Sarro de Si Mesma",
            tag = "Meta-Humor",
            summary = "Ao rolar os feeds das redes sociais no celular do jogo, você encontra cidadãos de Leonida furiosos reclamando que 'Race and Chase 7' foi adiado de novo — 'Race'n'Chase' era o nome original de desenvolvimento do primeiro GTA de 1997!",
            whereToSpot = "Feeds sociais no smartphone in-game.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_ringtone",
            title = "O Toque Clássico 'Windchimes'",
            tag = "Detalhe Oculto",
            summary = "Nas configurações de som do celular de Jason ou Lucia, você pode escolher o toque 'Windchimes' — é exatamente o mesmo ringtone característico que Michael De Santa usava em Grand Theft Auto V.",
            whereToSpot = "Celular > Configurações > Toques.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_vc_2002",
            title = "Portal Visual para Vice City de 2002",
            tag = "Easter Egg Épico",
            summary = "Resolver o enigma das subestações elétricas (inspirado na lógica clássica de cheats de Vice City) abre um portal surreal que joga Jason brevemente em uma versão retrô com gráficos de 2002 e libera a camisa havaiana do Tommy Vercetti.",
            whereToSpot = "Começa no topo da Torre de Rádio de Key Lento.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_cool_dog",
            title = "Existe um Cachorro Salva-Vidas de Jet Ski",
            tag = "Mundo Vivo",
            summary = "Se você ficar perdido nadando longe demais da costa de Leonida, pode escutar latidos em alto-mar: o 'Cool Dog', um cachorro de óculos pilotando um jet ski, vem resgatar você e te leva para uma ilha secreta de cães cheia de dinheiro!",
            whereToSpot = "Mar aberto distante da costa das Keys.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_bonnie_clyde",
            title = "De Vários Protagonistas para Bonnie & Clyde",
            tag = "Bastidores",
            summary = "Diferente de GTA V com 3 histórias paralelas, a Rockstar focou GTA VI na dinâmica íntima de casal fora-da-lei entre Jason e Lucia — sendo Lucia a primeira protagonista feminina jogável da era moderna da franquia.",
            whereToSpot = "Interações em dupla e atividades de parceiro pelo mapa.",
            minChapter = 0
        ),
        FunFact(
            id = "fact_arthur_hat",
            title = "O Chapéu de Arthur Morgan Sobreviveu ao Tempo",
            tag = "Red Dead 2",
            summary = "Em uma venda de garagem no sul de Port Gellhorn, o lendário chapéu de couro de Arthur Morgan (Red Dead Redemption 2) descansa sobre um case de violão em cima de um sofá antigo.",
            whereToSpot = "Sul de Port Gellhorn (Kelly County).",
            minChapter = 2
        ),
        FunFact(
            id = "fact_meditation_double",
            title = "Meditar com os Dois Conta em Dobro",
            tag = "Truque do 100%",
            summary = "Para acelerar o requisito de pontos de meditação no checklist de 100%, completar uma sessão no mesmo local primeiro com Jason e depois com Lucia contabiliza em dobro no progresso!",
            whereToSpot = "Pontos de meditação espalhados pelas praias e montanhas.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_skunk_ape",
            title = "O Folclore Real da Flórida: Skunk Ape",
            tag = "Cultura Local",
            summary = "Em vez do tradicional Pé-Grande das montanhas, os pântanos de Grassrivers abrigam o 'Skunk Ape' — uma lenda urbana real dos Everglades na Flórida famosa pelo odor sulfuroso e pegadas gigantes na lama.",
            whereToSpot = "Manguezais de Cottonmouth Lake / Gullet Marsh.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_61_cheats",
            title = "O Maior Sistema de Cheats Progressivos da Série",
            tag = "Mecânica Nova",
            summary = "GTA VI aboliu os números de telefone discáveis de GTA V e criou 61 cheats ativados por combinações no D-Pad, mas transformou cada um deles em recompensa pós-campanha ligada a desafios reais do mundo aberto.",
            whereToSpot = "Menu Progresso > Cheats (após concluir a campanha).",
            minChapter = 0
        ),
        FunFact(
            id = "fact_dexter_slice",
            title = "Homenagem Completa a Dexter em Vice Beach",
            tag = "Cultura Pop",
            summary = "O prédio 'Trinity Apartments' em Washington Island não só replica a varanda do apartamento de Dexter Morgan como esconde um kit completo no telhado e o barco homônimo ao 'Slice of Life' no píer ao lado.",
            whereToSpot = "Washington Island, Vice City.",
            minChapter = 1
        ),
        FunFact(
            id = "fact_200_hours",
            title = "Mais de 200 Horas para a Platina",
            tag = "Escala Épica",
            summary = "A campanha principal tem 91 missões e dura em média de 60 a 70 horas. Já quem mira o 100% completo e o troféu de ouro 'A Long Road' facilmente ultrapassa a marca de 200 horas explorando Leonida.",
            whereToSpot = "Progresso > Estatísticas > 100%.",
            minChapter = 0
        )
    )

    // =========================================================================
    // 6. DICAS TÁTICAS PARA A JOGATINA DO DIA A DIA
    // =========================================================================
    val dailyTips: List<GameplayTip> = listOf(
        GameplayTip(
            id = "tip_waink_early",
            title = "Escaneie TUDO no App WAiNK Desde o Dia 1",
            badge = "100% Sem Dor",
            body = "Não deixe para o pós-game! Sempre que cruzar com um carro, caminhão, barco ou avião diferente, saque o celular e escaneie no WAiNK. Chegar a 200 de 277 modelos fica natural e sem grind tedioso."
        ),
        GameplayTip(
            id = "tip_binoculars",
            title = "Binóculos = Fauna Fácil (Sem Caçar)",
            badge = "100% Sem Dor",
            body = "Para bater a meta de 100 de 255 animais estudados, você NÃO precisa matar nem esfolar ninguém. Basta apontar os binóculos para pássaros, jacarés, peixes e cervos enquanto viaja entre missões."
        ),
        GameplayTip(
            id = "tip_stash_cash",
            title = "Guarde Seu Dinheiro — Não Ande Cheio de Notas",
            badge = "Sobrevivência",
            body = "Diferente de jogos mais permissivos, ser preso ou abatido carregando toda a sua fortuna sai muito caro em GTA VI. Deposite ou guarde seu dinheiro sempre que finalizar um golpe."
        ),
        GameplayTip(
            id = "tip_suppressor",
            title = "Compre um Silenciador Logo em Key Lento",
            badge = "Combate",
            body = "Na primeira quinta parte do jogo nas Keys, visite a Ammu-Nation e equipe silenciador na sua pistola/rifle. Eliminar sentinelas em silêncio evita tiroteios intermináveis e reduz o alerta policial."
        ),
        GameplayTip(
            id = "tip_criminal_profile",
            title = "Cuide do Seu Perfil Criminal",
            badge = "Furtividade",
            body = "Suas roupas, veículos recorrentes e padrão de crimes ficam registrados no sistema de Perfil Criminal. Trocar de visual e de carro após um grande assalto ajuda a despistar testemunhas e patrulhas."
        ),
        GameplayTip(
            id = "tip_day_night",
            title = "O Horário Muda Tudo nos Assaltos",
            badge = "Planejamento",
            body = "A densidade de civis, lojas abertas e rotas de resposta policial variam drasticamente entre o dia e a madrugada. Observe o relógio antes de invadir bancos ou joalherias."
        ),
        GameplayTip(
            id = "tip_cheat_warning",
            title = "Save Manual Antes de Qualquer Cheat!",
            badge = "Alerta Crítico",
            body = "Usar qualquer um dos 61 cheats bloqueia saves automáticos/manuais e desativa troféus/conquistas até você reiniciar o jogo. Crie sempre um slot de save separado só para 'zoeira'."
        ),
        GameplayTip(
            id = "tip_no_missables",
            title = "Curta a História em Paz: Nada é Perdível",
            badge = "Zero Ansiedade",
            body = "Rockstar desenhou o checklist de 100% sem nenhum item perdível. Se você pular tarefas secundárias ou coletáveis durante um capítulo, tudo continua disponível e retroativo no pós-game!"
        )
    )

    // =========================================================================
    // 7. DOSSIÊ DE PERSONAGENS (SEM SPOILERS)
    // =========================================================================
    val characters: List<CharacterProfile> = listOf(
        CharacterProfile(
            id = "char_jason",
            name = "Jason Duval",
            role = "Protagonista Jogável",
            region = LeonidaRegion.LEONIDA_KEYS,
            bio = "Cresceu cercado por golpistas, tentou endireitar a vida no Exército dos EUA, mas acabou voltando à margem da lei como contrabandista e cobrador nas Leonida Keys até cruzar caminhos com Lucia.",
            minChapter = 0
        ),
        CharacterProfile(
            id = "char_lucia",
            name = "Lucia Caminos",
            role = "Protagonista Jogável",
            region = LeonidaRegion.LEONIDA_KEYS,
            bio = "Primeira protagonista feminina da era moderna de GTA. Aprendeu a lutar cedo com o pai, tem raízes em Liberty City e sai da Penitenciária de Leonida determinada a virar o jogo ao lado de Jason.",
            minChapter = 0
        ),
        CharacterProfile(
            id = "char_brian",
            name = "Brian Heder",
            role = "Fixer Veterano & Dono de Marina",
            region = LeonidaRegion.LEONIDA_KEYS,
            bio = "Veterano da era de ouro do contrabando nas Keys. Comanda a 'Brian's Boat Works & Marina', está no terceiro casamento (com Lori) e terceiriza o trabalho pesado para jovens como Jason e Cal.",
            minChapter = 1
        ),
        CharacterProfile(
            id = "char_cal",
            name = "Cal Hampton",
            role = "Amigo de Jason & Conspiracionista",
            region = LeonidaRegion.LEONIDA_KEYS,
            bio = "Caseiro convicto, fã de cerveja gelada, fóruns obscuros e rádio da Guarda Costeira. Acredita firmemente que o mundo é governado por psicopatas, mas é leal ao círculo próximo de Jason.",
            minChapter = 1
        ),
        CharacterProfile(
            id = "char_boobie",
            name = "Boobie Ike",
            role = "Lenda de Vice City & Empresário",
            region = LeonidaRegion.VICE_CITY,
            bio = "Milionário carismático que converteu a antiga vida nas ruas em um império legítimo de imóveis, clube noturno e a gravadora Only Raw Records.",
            minChapter = 3
        ),
        CharacterProfile(
            id = "char_drequan",
            name = "Dre'Quan Priest",
            role = "Produtor & Sócio de Boobie Ike",
            region = LeonidaRegion.VICE_CITY,
            bio = "Ambicioso aspirante a magnata da música em Vice City, conectado à cena urbana da metrópole e parceiro comercial de Boobie Ike na Only Raw Records.",
            minChapter = 3
        )
    )
}
