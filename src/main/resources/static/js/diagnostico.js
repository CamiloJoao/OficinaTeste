// Apoio ao diagnóstico: enquanto a IA responde, o botão fica desabilitado
// e aparece o aviso de espera.
document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("form-diagnostico");
    const aguarde = document.getElementById("aguarde");

    // Serviço encerrado: a tela não tem o formulário
    if (!form) return;

    const botao = form.querySelector("button");
    const textoOriginal = botao.textContent;

    form.addEventListener("submit", function () {
        botao.disabled = true;
        botao.textContent = "Gerando sugestão...";
        aguarde.hidden = false;
    });

    // Ao voltar pelo navegador, a página pode reaparecer com o botão ainda desabilitado
    window.addEventListener("pageshow", function () {
        botao.disabled = false;
        botao.textContent = textoOriginal;
        aguarde.hidden = true;
    });
});
