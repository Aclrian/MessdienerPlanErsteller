# MessdienerPlanErsteller
Erstellt einen Messplan für Messdiener

[Website](https://aclrian.github.io/MessdienerPlanErsteller/)

Dazu wird OpenJDK-17 benötigt. Für Einsteiger gibt es [Installationsinstruktionen](https://github.com/Aclrian/MessdienerPlanErsteller/wiki/Installationshinweise)

## Selber kompilieren

Es wird Java JDK17 und Maven benötigt.

```
git clone https://github.com/Aclrian/MessdienerPlanErsteller.git
cd MessdienerPlanErsteller/
git checkout develop            # optional, für neuste Änderungen

maven clean javafx:run
maven clean javafx:run@debug    # zum Debuggen
maven test
```
