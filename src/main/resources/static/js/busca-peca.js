// Busca em selects: todo <input class="busca-peca" data-alvo="ID">
// filtra as opções do <select id="ID"> conforme o texto digitado.
(function () {

    function prepararBusca(input) {
        const select = document.getElementById(input.getAttribute("data-alvo"));

        // Sem select correspondente, não faz nada
        if (select == null) {
            return;
        }

        // Guarda todas as opções originais do select
        const originais = [];
        for (let i = 0; i < select.options.length; i++) {
            originais.push(select.options[i]);
        }

        input.addEventListener("input", function () {
            const termo = input.value.trim().toLowerCase();
            const selecionado = select.value;
            let selecionadoNaLista = false;

            // Reconstrói o select só com as opções que contêm o termo
            select.innerHTML = "";
            for (let i = 0; i < originais.length; i++) {
                const opcao = originais[i];
                const texto = opcao.text.toLowerCase();

                // A opção vazia ("Selecione") sempre permanece
                if (opcao.value === "" || texto.includes(termo)) {
                    select.appendChild(opcao);

                    if (opcao.value === selecionado) {
                        selecionadoNaLista = true;
                    }
                }
            }

            // Mantém a peça escolhida se ela continuar na lista
            if (selecionadoNaLista) {
                select.value = selecionado;
            } else {
                select.value = "";
            }
        });
    }

    const buscas = document.querySelectorAll("input.busca-peca");
    for (let i = 0; i < buscas.length; i++) {
        prepararBusca(buscas[i]);
    }
})();
