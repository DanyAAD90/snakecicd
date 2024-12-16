# Zalozenia projektu gry snake w przegladarce:
- Repozytorium w folderze snake/ z plikami oraz docker-compose to utworzenia jenkins w kontenerze oraz index.html z gra snake
- kazda zmiana w kodzie index.html lub w ogole folderze powoduje automatyczne pushowanie do github na podstawie dodanego serwisu auto_commit.service w systemd
- Otwieramy jenkins w przegladarce pod adresem 192.168.18.165:8003 i uruchamiamy pipeline o nazwie "snake_test_auto_image"
## Continous Integration + Continous Delivery na dockerhub pipeline:
  - kopiuje repo z github do swojej przestrzeni kontenera jenkins w /var/jenkins_home/workspace/snake_test_auto_image/
  - testuje kod index.html -> jesli blad składni to zatrzymuje proces
  - tworzy obraz docker np z wersja 15
  - pushuje nowa wersje na dockerhub (Continous Delivery)
  - sprawdza czy jest polaczenie curl
  - uruchamia aplikacje snake na stagingu pod adresem 192.168.18.165:8215
  - zapisuje logi
## Realizacja calkowitej automatyzacji Continous Delivery na kubernetes:
  - uruchomienie monitor_repo.sh w snakecicd_monitor/ sprawdza czy nastapila zmiana w kodzie gry (index.html), jeśli tak to wykonaj pipeline
  - uruchomienie monitor.sh w snakecicd_monitor/ sprawdza zmianę wersji dostarczoną przez jenkins, która pojawi się po wykonaniu pipeline
  - aplikacja jest gotowa na produkcji pod adresem http://192.168.18.165:8315
