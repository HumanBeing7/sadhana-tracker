# Sadhaka Registration API - Sample JSON Files

## 📋 **Registration Endpoint**
- **URL:** `POST /api/auth/register`
- **Content-Type:** `application/json`
- **Authentication:** Not required (public endpoint)

## 📁 **Sample JSON Files**

### 1. **register-sadhaka.json** - Basic Registration
Standard registration for a first-initiated devotee from ISKCON Vrindavan.

### 2. **register-sadhaka-advanced.json** - Advanced Devotee
Registration for a second-initiated devotee from ISKCON New York with spiritual name.

### 3. **register-sadhaka-beginner.json** - Beginner Devotee
Registration for an aspiring devotee from ISKCON Mayapur.

## 🔐 **Required Fields**

### **SadhakaDTO Fields:**
- `username` (string) - Unique username for login
- `firstName` (string) - First name
- `lastName` (string) - Last name  
- `email` (string) - Email address (must be unique)
- `phoneNumber` (string) - Contact number
- `spiritualName` (string) - Spiritual/initiated name
- `temple` (string) - Home temple/center
- `initiationLevel` (string) - Spiritual initiation level
- `location` (string) - Current location
- `role` (string) - Must be "SADHAKA" for devotees
- `active` (boolean) - Account status (true/false)

### **Additional Fields:**
- `password` (string) - Account password (will be encrypted)

## 📝 **Initiation Levels**
- "Aspiring Devotee" - Not yet initiated
- "First Initiation" - Received first diksha
- "Second Initiation" - Received second diksha  
- "Sannyasi" - Renounced order

## 🏛️ **Common ISKCON Temples**
- ISKCON Vrindavan
- ISKCON Mayapur  
- ISKCON Mumbai
- ISKCON Delhi
- ISKCON Bangalore
- ISKCON New York
- ISKCON Los Angeles
- ISKCON London

## 🌍 **Testing with cURL**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d @register-sadhaka.json
```

## 📱 **Testing with Postman**
1. Set method to `POST`
2. URL: `http://localhost:8080/api/auth/register`
3. Headers: `Content-Type: application/json`
4. Body: Copy content from any sample JSON file

## ✅ **Expected Success Response**
```json
{
  "message": "Registration successful",
  "data": {
    "id": 1,
    "username": "krishna_das_123",
    "firstName": "Krishna",
    "lastName": "Das",
    "email": "krishna.das@example.com",
    "spiritualName": "Krishna Das",
    "temple": "ISKCON Vrindavan",
    "role": "SADHAKA",
    "active": true
  }
}
```

## ❌ **Common Error Responses**
- **400 Bad Request:** Validation errors, duplicate username/email
- **500 Internal Server Error:** Database connection issues

---
**🙏 Hare Krishna! May your registration be successful in serving the Lord! 🙏**
