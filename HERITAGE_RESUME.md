# Structure d'héritage - Résumé

## Base de données (PostgreSQL)
Toutes les tables héritent de `users` avec l'héritage PostgreSQL :

### Table parent : `users`
```sql
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);
```

### Tables héritières :
- `admins` INHERITS (users)
- `managers` INHERITS (users)  
- `tellers` INHERITS (users)
- `auditors` INHERITS (users)
- `clients` INHERITS (users) + attributs spécifiques (national_id, monthly_income, email, phone, birth_date)

## Code Java

### Classes qui héritent de User :
✅ **Admin extends User**
✅ **Manager extends User** 
✅ **Teller extends User**
✅ **Auditor extends User**

### Classe autonome :
❌ **Client** - Classe autonome avec tous les attributs User directement intégrés
- Ne hérite PAS de User dans le code Java
- Hérite uniquement dans la base de données PostgreSQL
- Contient tous les attributs User + attributs spécifiques Client

## Avantages de cette approche :

1. **Flexibilité** : Client peut avoir une logique métier différente sans contraintes d'héritage
2. **Performance** : Pas de surcharge d'héritage pour la classe Client la plus utilisée
3. **Compatibilité DB** : L'héritage PostgreSQL permet des requêtes sur tous les utilisateurs
4. **Cohérence** : Les rôles administratifs partagent naturellement la logique User

## Utilisation :

```java
// Les classes admin héritent de User
Admin admin = new Admin(id, "admin", "pass", "Admin User");
User user = admin; // Polymorphisme possible

// Client est autonome
Client client = new Client();
client.setUsername("client1");
// client instanceof User == false (ne compile pas)
```