# Contexto do projeto
Sistema web interno para oficina de hardware (TCC de 3 alunos da FATEC, nível de fim de graduação).
Stack: Java 17, Spring Boot 3.5.3, Thymeleaf, Spring Data JPA, PostgreSQL. Sem API REST, sem framework JS.
Padrão das telas: todo controller devolve "layout" e define model.addAttribute("pagina", "<nome-do-template>").
O catálogo de peças fica em resources/regras/compatibilidade.json (não está no banco).

# Regras para qualquer tarefa
- Código simples, que um estudante consiga explicar numa banca: prefira for/if a streams, sem generics novos, sem bibliotecas novas.
- Siga o estilo dos arquivos existentes (nomes em português, comentários curtos em português, sem emojis).
- Altere SOMENTE os arquivos citados na tarefa. Se precisar mexer em outro, pergunte antes (outros colegas trabalham em paralelo).
- Antes de alterar, leia os arquivos envolvidos e me mostre um plano curto. Só implemente depois que eu aprovar.
- Ao terminar, liste o que mudou em cada arquivo e explique em 2-3 frases a lógica, para eu conseguir defender na banca.