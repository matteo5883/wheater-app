# Test Configuration Guide

Questo progetto utilizza diversi profili di configurazione per supportare vari
tipi di test.

## File di Configurazione Disponibili

### 📋 `application.yml` (Test di Base)

- **Profilo**: `test` (default per i test)
- **Utilizzo**: Test unitari e di base
- **Caratteristiche**:
    - Mock mode abilitato
    - Timeout ridotti per velocità
    - Cache di dimensioni ridotte
    - Logging ottimizzato per debug

### 🔗 `application-integration-test.yml`

- **Profilo**: `integration-test`
- **Utilizzo**: Test di integrazione
- **Caratteristiche**:
    - Mock mode disabilitato
    - Timeout più lunghi per chiamate reali
    - Logging più verboso
    - Configurazione realistica

### ⚡ `application-performance-test.yml`

- **Profilo**: `performance-test`
- **Utilizzo**: Test di performance e carico
- **Caratteristiche**:
    - Cache di grandi dimensioni
    - Timeout estesi
    - Logging minimale per ridurre overhead
    - Configurazione ottimizzata per performance

## Come Utilizzare i Profili

### In Test Unitari

```java
@SpringBootTest
@ActiveProfiles("test")  // Usa il profilo di test di base
class MyUnitTest {
    // ...
}
```

### In Test di Integrazione

```java
@SpringBootTest
@ActiveProfiles("integration-test")  // Usa il profilo di integrazione
class MyIntegrationTest {
    // ...
}
```

### In Test di Performance

```java
@SpringBootTest
@ActiveProfiles("performance-test")  // Usa il profilo di performance
class MyPerformanceTest {
    // ...
}
```

### Dalla Riga di Comando

```bash
# Test con profilo specifico
./gradlew test -Dspring.profiles.active=integration-test

# Test di performance
./gradlew test -Dspring.profiles.active=performance-test --tests "*PerformanceTest*"
```

## Configurazioni Principali per Profilo

| Configurazione   | Test        | Integration Test | Performance Test |
|------------------|-------------|------------------|------------------|
| Mock Mode        | ✅ Abilitato | ❌ Disabilitato   | ✅ Abilitato      |
| Connect Timeout  | 2s          | 10s              | 30s              |
| Read Timeout     | 5s          | 20s              | 60s              |
| Cache Size       | 100         | 50               | 10,000           |
| Cache Expiration | 5min        | 1min             | 60min            |
| Log Level        | DEBUG       | TRACE            | INFO             |
| Retry Attempts   | 1           | 2                | 5                |

## Variabili d'Ambiente per Test

Per i test di integrazione che richiedono API key reali:

```bash
export WEATHER_API_KEY=your-real-api-key
./gradlew test --tests "*IntegrationTest*"
```

## Best Practices

1. **Test Unitari**: Usa il profilo `test` di default
2. **Test di Integrazione**: Usa `integration-test` con API key reali
3. **Test di Performance**: Usa `performance-test` con mock per consistenza
4. **CI/CD**: Usa `test` per pipeline veloci, `integration-test` per release

## Struttura dei File di Test

```
src/test/resources/
├── application.yml                      # Test di base
├── application-integration-test.yml     # Test di integrazione  
├── application-performance-test.yml     # Test di performance
└── logback-test.xml                    # Configurazione logging test
```
