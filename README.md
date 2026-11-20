# Lava Rápido MVC

Aplicação desktop Java Swing para cadastro de clientes, serviços e agendamentos de um lava-rápido.

## Executar no NetBeans

1. Extraia o projeto em uma pasta local.
2. No NetBeans, selecione **File > Open Project** e escolha a pasta extraída `LavaRapidoMVC`.
3. Caso importe o arquivo ZIP, use **File > Import Project > From ZIP** e selecione o pacote atualizado. O arquivo `pom.xml` permite que o NetBeans reconheça o projeto automaticamente.
4. Confirme que o JDK 17 está selecionado em **Project Properties > Libraries**.
5. Clique em **Run Project** ou pressione F6.

O projeto já inclui os arquivos de configuração do NetBeans e não depende de bibliotecas externas.

## Executar no PowerShell

```powershell
./run.ps1
```

Os dados são guardados automaticamente em `data/lavarapido.dat`.
