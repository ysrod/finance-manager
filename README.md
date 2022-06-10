"# finance-manager" 
"# finance-manager" 
"# finance-manager" 
"# finance-manager" 
"# finance-manager" 
"# finance-manager"

Aplicativo fazer:

Design telas;

Main Activity:
- Mostrar saldo do usuário a partir de cálculo Receitas - Despesas;
- Botões enviarem para a Finance Activity o estado do switch que indica se é uma despesa ou receita.
- Recyclerview mostrar 5 itens nos quais "type = false" (despesa);
- Recyclerview mostrar 5 itens nos quais "type = true" (receita);

Finance Activity:
- Fazer com que o switch mude de estado (atributo "type" de Finance) dependendo da origem do usuário:
  -- Botão cadastrar receita: switch true;
  -- Botão cadastrar despesa: switch false;
  -- Floating button de RevenueActivity: switch true;
  -- Floating button de ExpenseActivity: switch false;
  -- Porém, ser possível que o usuário mude o switch caso queira, mesmo vindo de uma tela diferente.

- Fazer com que os campos sejam preenchidos com os detalhes de um item já existente/o nome do botão mude caso o usuário venha:
  -- De um click simples em um item na lista de RevenueActivity;
  -- De um click simples em um item na lista de ExpenseActivity;


Revenue Activity:
- Lista (recyclerview) com todos os itens nos quais "type = true";
- Poder clicar uma vez em um item e abrir FinanceActivity com os campos preenchidos;
- Segurar o clique e poder deletar um item;

Expense Activity:
- Lista (recyclerview) com todos os itens nos quais "type = false";
- Poder clicar uma vez em um item e abrir FinanceActivity com os campos preenchidos;
- Segurar o clique e poder deletar um item.


Problemas:
Finance Activity:
-- Quando o usuário vem da tela principal (Main Activity) a partir de um dos recyclerviews, e vai ou para a tela de RevenueActivity ou ExpenseActivity, e, depois, a partir de uma dessas telas, tenta cadastrar nova despesa ou receita na tela Finance Activity, a pop-up "[tipo de item] foi cadastrada com sucesso!" não substitui corretamente o que está entre brackets pelas palavras "Receita" ou "Despesa". Esse erro não ocorre se o usuário vier diretamente dos botões "Cadastrar Receita" ou "Cadastrar Despesa" direto da tela principal, sem passar pelas telas intermediárias.

-- O switch não está sendo mudado conforme os atributos de true ou false quando o usuário vem de telas de despesa ou receita, mesmo quando estes são recebidos corretamente de uma tela para a outra (e, quando um novo item é salvo no Firebase, ele reflete corretamente se o switch estava em true ou false, o problema é que esses valores não estão fazendo o switch se mexer com o método .isActivated)

-- A tela de Receitas está mostrando itens classificados como despesa (type == false)  mesmo com uma checagem na hora de chamar o adapter.

-- Ao acionar o switch antes de cadastrar um item novo, às vezes "abrem-se" novas instâncias da própria FinanceActivity por cima da instância atual [não sei exatamente onde ocorre, preciso tentar recriar mais vezes depois].