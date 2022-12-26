FROM mcr.microsoft.com/dotnet/sdk:5.0 AS build-dev
WORKDIR /app

COPY DigiBrainServer.csproj .
RUN dotnet restore

COPY . ./

RUN dotnet publish DigiBrainServer.csproj -c Release -o /app/out

FROM mcr.microsoft.com/dotnet/aspnet:5.0
WORKDIR /app
COPY --from=build-dev /app/out .

ENTRYPOINT ["dotnet", "DigiBrainServer.dll", "--migrate"]
