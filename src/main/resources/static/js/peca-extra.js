// "Adicionar peça" nas telas de detalhe: o select de peças mostra só as do tipo escolhido
// que contêm o texto da busca.
document.addEventListener("DOMContentLoaded", function () {

    const tipoSelect = document.getElementById("tipo");
    const selectPeca = document.getElementById("peca");
    const container = document.getElementById("containerPeca");
    const buscaPeca = document.getElementById("buscaPeca");

    if (!container) return;

    // Mostra só as peças do tipo escolhido que contêm o texto da busca
    function aplicarFiltros() {

        const tipoSelecionado = tipoSelect.value;
        const termo = buscaPeca.value.trim().toLowerCase();
        const opcoes = selectPeca.options;

        for (let i = 0; i < opcoes.length; i++) {

            const tipoOpcao = opcoes[i].getAttribute("data-tipo");
            const texto = opcoes[i].text.toLowerCase();

            opcoes[i].hidden = tipoOpcao !== tipoSelecionado || !texto.includes(termo);
        }
    }

    function filtrarPecas() {

        const tipoSelecionado = tipoSelect.value;

        if (!tipoSelecionado) {
            container.style.display = "none";
        } else {
            container.style.display = "block";
        }

        // Trocou o tipo: a busca recomeça
        buscaPeca.value = "";
        aplicarFiltros();

        selectPeca.selectedIndex = 0;
    }

    // Ao digitar: se a peça escolhida sumiu da busca, desfaz a escolha
    function filtrarPorTexto() {

        aplicarFiltros();

        if (selectPeca.selectedIndex > 0 && selectPeca.options[selectPeca.selectedIndex].hidden) {
            selectPeca.selectedIndex = 0;
        }
    }

    tipoSelect.addEventListener("change", filtrarPecas);
    buscaPeca.addEventListener("input", filtrarPorTexto);

    filtrarPecas();
});
