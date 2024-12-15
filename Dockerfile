#Pobierz oficjalny obraz nginx
FROM nginx:alpine

# Skopiuj plik index.html do katalogu, w którym nginx będzie go serwować
COPY index.html /usr/share/nginx/html/index.html

# Zmiana domyślnego portu na 80
EXPOSE 80

# Uruchom serwer nginx, który będzie nasłuchiwał na porcie 8016
CMD ["nginx", "-g", "daemon off;"]

#test automatycznego pushowania
