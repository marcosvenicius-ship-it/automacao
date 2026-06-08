async function analisarConversaComIA() {
    const conversaTexto = document.getElementById("conversa-chat").value;
    const botaoIA = document.querySelector("button.botao-ia"); 

    if (!conversaTexto.trim()) {
        alert("Por favor, cole alguma conversa antes de analisar.");
        return;
    }

    botaoIA.innerText = "Analisando... Aguarde.";
    botaoIA.disabled = true;

    try {
        const response = await fetch("https://automacao-crm-backend.onrender.com/api/analisar-chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ conversa: conversaTexto })
        });

        if (!response.ok) {
            throw new Error("Erro na comunicação com o servidor Java.");
        }

        const textoResposta = await response.text();

        const dadosPreenchidos = JSON.parse(textoResposta);

        document.getElementById("razao-social").value = dadosPreenchidos.razaoSocial || "";
        document.getElementById("cnpj").value = dadosPreenchidos.cnpj || "";
        document.getElementById("nome").value = dadosPreenchidos.nome || "";
        document.getElementById("cargo").value = dadosPreenchidos.cargo || "";
        document.getElementById("telefone").value = dadosPreenchidos.telefone || "";
        document.getElementById("email").value = dadosPreenchidos.email || "";
        document.getElementById("segmento").value = dadosPreenchidos.segmento || "";
        document.getElementById("colaboradores").value = dadosPreenchidos.colaboradores || "";
        document.getElementById("faturamento").value = dadosPreenchidos.faturamento || "";
        document.getElementById("necessidade").value = dadosPreenchidos.necessidade || "";
        document.getElementById("sistemaAtual").value = dadosPreenchidos.sistemaAtual || "";

        if (typeof gerarResumo === "function") {
            gerarResumo();
        }

    } catch (erro) {
        console.error(erro);
        alert("Houve um problema ao processar as informações da IA, mas o servidor está online!");
    } finally {
        botaoIA.innerText = "Analisar conversa";
        botaoIA.disabled = false;
    }
}


function gerarResumo() {
    const razaoSocial = document.getElementById("razao-social").value;
    const cnpj = document.getElementById("cnpj").value;
    const nome = document.getElementById("nome").value;
    const cargo = document.getElementById("cargo").value;
    const telefone = document.getElementById("telefone").value;
    const email = document.getElementById("email").value;
    const segmento = document.getElementById("segmento").value;
    const colaboradores = document.getElementById("colaboradores").value;
    const faturamento = document.getElementById("faturamento").value;
    const necessidade = document.getElementById("necessidade").value;
    const sistemaAtual = document.getElementById("sistemaAtual").value;

    const resumo = `CNPJ - ${cnpj} 
    
RAZÃO SOCIAL - ${razaoSocial} 
    
FALEI COM ${nome}, ${cargo}, NO TELEFONE ${telefone} E E-MAIL ${email}. 
A EMPRESA ATUA NO RAMO DE ${segmento}. 
SÃO ${colaboradores} COLABORADORES E A ESTIMATIVA DE FATURAMENTO ANUAL FICA EM TORNO DE ${faturamento}. 
NOS ACIONOU COM INTERESSE EM UM SISTEMA PARA ${necessidade}. 
ATUALMENTE UTILIZAM O SISTEMA ${sistemaAtual}.`;

    document.getElementById("resultado").innerText = resumo.trim().toUpperCase();
}

function limparFormulario() {
    document.getElementById("conversa-chat").value = "";
    document.getElementById("razao-social").value = "";
    document.getElementById("cnpj").value = "";
    document.getElementById("nome").value = "";
    document.getElementById("cargo").value = "";
    document.getElementById("telefone").value = "";
    document.getElementById("email").value = "";
    document.getElementById("segmento").value = "";
    document.getElementById("colaboradores").value = "";
    document.getElementById("faturamento").value = "";
    document.getElementById("necessidade").value = "";
    document.getElementById("sistemaAtual").value = "";
    document.getElementById("resultado").innerText = "";
}
