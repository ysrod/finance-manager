"# finance-manager" 
"# finance-manager" 
"# finance-manager" 
"# finance-manager" 
"# finance-manager" 
"# finance-manager"

Aplicativo fazer:

Design telas;

Register Activity:
- Não permitir que o usuário se cadastre com um e-mail já existente no firebase
- Não permitir senhas com menos de 6 caracteres


Main Activity:
- Mostrar saldo do usuário a partir de cálculo Receitas - Despesas feito no atributo 'saldo';
- Fechar aplicativo caso aperte 'voltar' nesta tela;
OK - Botões enviarem para a Finance Activity o estado do switch que indica se é uma despesa ou receita.
- Recyclerview mostrar 5 itens nos quais "type = false" (despesa);
- Recyclerview mostrar 5 itens nos quais "type = true" (receita);
- Obs.: não será usado o cardview das revenue e expense activities pra preencher, pois são muito grandes e ocupam mt espaço

Finance Activity:
-- Facilitar o preenchimento do campo 'valor'
-- Facilitar o preenchimento do campo 'data'
-- Voltar para a tela correta ao se cadastrar uma finança, ex.: cadastrou despesa -> vai pra expense activity

OK - Fazer com que o switch mude de estado (atributo "type" de Finance) dependendo da origem do usuário:
      -- Botão cadastrar receita: switch true;
      -- Botão cadastrar despesa: switch false;
      -- Floating button de RevenueActivity: switch true;
      -- Floating button de ExpenseActivity: switch false;
      -- Porém, ser possível que o usuário mude o switch caso queira, mesmo vindo de uma tela diferente.

OK - Fazer com que os campos sejam preenchidos com os detalhes de um item já existente/o nome do botão mude caso o usuário venha:
      -- De um click simples em um item na lista de RevenueActivity;
      -- De um click simples em um item na lista de ExpenseActivity;


Revenue Activity:
- Organizar os itens segundo a data

OK - Lista (recyclerview) com todos os itens nos quais "type = true";
OK - Poder clicar uma vez em um item e abrir FinanceActivity com os campos preenchidos;
OK - Segurar o clique e poder deletar um item;

Expense Activity:
- Organizar os itens segundo a data

OK - Lista (recyclerview) com todos os itens nos quais "type = false";
OK - Poder clicar uma vez em um item e abrir FinanceActivity com os campos preenchidos;
OK - Segurar o clique e poder deletar um item.