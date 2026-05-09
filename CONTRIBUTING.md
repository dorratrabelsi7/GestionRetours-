# 🤝 Guide de Contribution

Merci d'être intéressé par la contribution à ce projet! Veuillez lire ce guide pour comprendre comment contribuer efficacement.

## 📋 Avant de commencer

- Consultez les [Issues](https://github.com/dorratrabelsi7/GestionRetours-/issues) pour voir ce qui doit être fait
- Lisez le [README.md](README.md) et [ARCHITECTURE.md](ARCHITECTURE.md)
- Comprenez la structure du projet

## 🔧 Configuration du Développement

### 1. Cloner le Repository
```bash
git clone https://github.com/dorratrabelsi7/GestionRetours-.git
cd Backend_Projet
```

### 2. Installer les Dépendances
```bash
mvn install
```

### 3. Configurer l'IDE
- Utilisez Eclipse IDE ou IntelliJ IDEA
- Installer Lombok Plugin
- Configurer le formatter de code

### 4. Lancer l'Application
```bash
# Avec Docker Compose (recommandé)
docker-compose up -d

# Ou localement
mvn clean spring-boot:run
```

## 📝 Workflow Git

### 1. Créer une Branche Feature
```bash
git checkout -b feature/nom-de-la-feature
# Ou pour un bugfix
git checkout -b bugfix/nom-du-bug
```

### 2. Faire les Modifications
- Modifiez les fichiers nécessaires
- Testez vos modifications
- Vérifiez la compilation

### 3. Commiter les Changements
```bash
git add .
git commit -m "type(scope): description"
```

### 4. Pousser vers le Repository
```bash
git push origin feature/nom-de-la-feature
```

### 5. Créer une Pull Request
- Allez sur GitHub
- Créez une PR vers la branche `main`
- Décrivez les changements
- Attendez la review

## 📌 Convention de Commits

Nous suivons [Conventional Commits](https://www.conventionalcommits.org/):

### Format
```
type(scope): subject

body

footer
```

### Types
- **feat**: Nouvelle fonctionnalité
- **fix**: Correction de bug
- **docs**: Changements de documentation
- **style**: Formattage du code (sans impact fonctionnel)
- **refactor**: Refactoring du code
- **perf**: Améliorations de performance
- **test**: Ajout/modification de tests
- **chore**: Mise à jour des dépendances, scripts, etc.

### Exemples
```bash
git commit -m "feat(retour): ajouter validation de la quantité"
git commit -m "fix(non-conformite): corriger le calcul de gravité"
git commit -m "docs(api): mettre à jour le guide des endpoints"
git commit -m "refactor(services): extraire logique commune"
```

## 🏗️ Structure du Code

### Créer une Nouvelle Entité
1. Créer la classe dans `entities/`
2. Ajouter les annotations JPA
3. Ajouter les validations
4. Créer le Repository dans `repositories/`
5. Créer le DTO dans `dto/`
6. Créer le Service dans `services/`
7. Créer le Controller dans `controllers/`
8. Créer le Convertor dans `convertors/`

### Exemple pour EntitéExample
```java
// 1. Entity
@Entity
@Data
public class Example {
    @Id @GeneratedValue
    private int id;
    // attributs...
}

// 2. Repository
@Repository
public interface ExampleRepository extends JpaRepository<Example, Integer> {
    // méthodes personnalisées...
}

// 3. DTO
@Data
@Validated
public class ExampleDTO {
    @NotBlank
    private String field;
    // validations...
}

// 4. Service
@Service
public class ExampleService {
    @Autowired
    private ExampleRepository repo;
    // logique métier...
}

// 5. Controller
@RestController
@RequestMapping("/examples")
public class ExampleController {
    @Autowired
    private ExampleService service;
    // endpoints...
}

// 6. Convertor
@Component
public class ExampleConvertor {
    @Autowired
    private ModelMapper mapper;
    // conversion...
}
```

## ✅ Checklist Avant Pull Request

- [ ] Code compilé sans erreurs
- [ ] Code formaté selon les conventions
- [ ] Tests écrits et vérifiés
- [ ] Documentation mise à jour
- [ ] Pas de warnings
- [ ] Commit messages clairs et concis
- [ ] Branche à jour avec main
- [ ] PR description détaillée

## 🧪 Tests

### Lancer les Tests
```bash
mvn test
```

### Écrire des Tests
Créez des tests dans `src/test/java/`:
```java
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService service;
    
    @Test
    public void testCreateUser() {
        // Arrange
        UserDTO dto = new UserDTO();
        
        // Act
        User user = service.create(dto);
        
        // Assert
        assertNotNull(user);
    }
}
```

## 📝 Style de Code

### Conventions
- **Indentation**: 4 espaces
- **Line Length**: 120 caractères maximum
- **Noms**: camelCase pour variables et méthodes, PascalCase pour classes
- **Constantes**: UPPER_SNAKE_CASE
- **Commentaires**: En français, clairs et utiles

### Exemple
```java
// Bon ✅
public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
    User user = userService.findById(id);
    return ResponseEntity.ok(userConvertor.toDto(user));
}

// Mauvais ❌
public ResponseEntity getUserById(@PathVariable int id){
    User u = serv.find(id);
    return ok(conv.to(u));
}
```

## 🐛 Signaler un Bug

1. Vérifiez que le bug n'existe pas déjà
2. Créez une issue avec le label `bug`
3. Décrivez:
   - Etapes pour reproduire
   - Comportement attendu
   - Comportement réel
   - Environnement (OS, Java, etc.)

## 💡 Suggérer une Feature

1. Créez une issue avec le label `enhancement`
2. Décrivez:
   - La fonctionnalité souhaitée
   - Son utilité
   - Des exemples d'utilisation
   - Les changements nécessaires

## 📚 Documentation

### Documenter les APIs
```java
@GetMapping("/{id}")
@Operation(summary = "Récupérer un utilisateur par ID")
@ApiResponse(responseCode = "200", description = "Utilisateur trouvé")
@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
    // ...
}
```

### Documenter le Code
```java
/**
 * Crée un nouvel utilisateur.
 * 
 * @param userDTO Les données de l'utilisateur
 * @return L'utilisateur créé
 * @throws Exception si les données sont invalides
 */
public User createUser(UserDTO userDTO) {
    // ...
}
```

## 🔄 Code Review Process

1. **Soumission**: Créez une PR
2. **Review**: Un reviewer examine le code
3. **Feedback**: Modifications demandées (si besoin)
4. **Corrections**: Vous corrigez le code
5. **Approbation**: Le reviewer approuve
6. **Merge**: PR mergée dans main

## ⚖️ License

Par la contribution, vous acceptez que votre code soit sous license MIT.

## 📞 Support

- 📧 Email: support@gestionretours.com
- 💬 Issues: [GitHub Issues](https://github.com/dorratrabelsi7/GestionRetours-/issues)
- 📚 Documentation: Voir README.md

---

## Ressources Utiles

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Swagger/OpenAPI](https://swagger.io/)
- [ModelMapper](http://modelmapper.org/)
- [Lombok](https://projectlombok.org/)

---

Merci de contribuer! 🙏

**Version**: 1.0.0  
**Dernière mise à jour**: 09 mai 2026
