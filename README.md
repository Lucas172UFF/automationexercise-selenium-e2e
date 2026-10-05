# Testes E2E — Automation Exercise

Projeto Maven com Selenium e JUnit 5 para os cenários solicitados do site [Automation Exercise](https://automationexercise.com/):

- **TC3 — Login com e-mail e senha incorretos:** cinco execuções parametrizadas, todas verificando a mensagem `Your email or password is incorrect!`.
- **TC1 — Registrar usuário:** criação de uma conta com e-mail único e exclusão da conta ao fim, para não deixar dados de teste no site.

## Pré-requisitos

- Java 17 ou superior;
- Google Chrome instalado;
- Maven 3.9 ou superior.

O Selenium Manager providencia o driver compatível automaticamente, portanto não é necessário baixar ou configurar o ChromeDriver manualmente.

## Execução

```bash
cd selenium-automationexercise
mvn test
```

Os testes rodam em modo *headless*. Para acompanhar o navegador, remova `--headless=new` em `AuthenticationE2ETest.java`.

## Técnicas de projeto de teste

| Entrada de e-mail no TC3 | Técnica / classe coberta | Resultado esperado |
|---|---|---|
| `usuario.inexistente@exemplo.com` | Classe válida: formato convencional | Login recusado |
| `a@exemplo.com` | Valor-limite inferior prático da parte local | Login recusado |
| 64 caracteres antes de `@` | Valor-limite superior usual da parte local | Login recusado |
| Rótulo de domínio com 63 caracteres | Valor-limite superior usual do rótulo DNS | Login recusado |
| `usuario+teste@exemplo.com` | Classe válida: alias com `+` | Login recusado |

Em todas as cinco entradas, a senha é uma senha válida em formato, mas inexistente. Assim, o teste isola a regra de autenticação do TC3 e confirma a mesma resposta de erro para representantes de classes válidas de e-mail. O TC1 complementa a suíte com o fluxo ponta a ponta de cadastro, autenticação e remoção da conta de teste.

## Entrega em repositório público

Após publicar esta pasta em uma conta GitHub, informe na atividade a URL do repositório público, por exemplo:

`https://github.com/SEU-USUARIO/automationexercise-selenium-e2e`
