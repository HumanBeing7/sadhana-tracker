# Admin Registration Guide

## 🔐 **How to Register as Admin**

### **Endpoint:** `POST /api/auth/register/admin`

---

## 📝 **Admin Registration**

### **Request Format:**
```http
POST /api/auth/register/admin
Content-Type: application/json

{
  "username": "admin",
  "password": "adminPassword123!",
  "firstName": "System",
  "lastName": "Administrator",
  "email": "admin@iskcon.org",
  "phoneNumber": "+1234567890"
}
```

### **Expected Response (Success):**
```json
{
  "message": "Admin registration successful",
  "data": {
    "id": 1,
    "username": "admin",
    "firstName": "System",
    "lastName": "Administrator",
    "email": "admin@iskcon.org",
    "phoneNumber": "+1234567890",
    "role": "ADMIN",
    "active": true,
    "createdAt": "2025-07-16T00:30:00",
    "updatedAt": "2025-07-16T00:30:00"
  }
}
```

### **Error Response (Username exists):**
```json
{
  "error": "Admin registration failed",
  "message": "Username already exists: admin"
}
```

---

## 🎯 **Complete Admin Setup Workflow**

### **Step 1: Register Admin**
```http
POST /api/auth/register/admin
{
  "username": "iskcon_admin",
  "password": "HareKrishna123!@#",
  "firstName": "ISKCON",
  "lastName": "Administrator",
  "email": "admin@iskcon.org"
}
```

### **Step 2: Login as Admin**
```http
POST /api/auth/login
{
  "username": "iskcon_admin",
  "password": "HareKrishna123!@#"
}
```

### **Step 3: Verify Admin Access**
```http
GET /api/admin/mentors
Authorization: Bearer {admin-jwt-token}
```

---

## 🚀 **Postman Testing Steps**

### **1. Create Admin Account**
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/register/admin`
- **Headers:** `Content-Type: application/json`
- **Body (JSON):**
```json
{
  "username": "admin",
  "password": "StrongPassword123!",
  "firstName": "Temple",
  "lastName": "Administrator",
  "email": "admin@example.com",
  "phoneNumber": "+1234567890"
}
```

### **2. Login with Admin Credentials**
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/login`
- **Body (JSON):**
```json
{
  "username": "admin",
  "password": "StrongPassword123!"
}
```

### **3. Test Admin Privileges**
- **Method:** GET
- **URL:** `http://localhost:8080/api/admin/sadhakas`
- **Headers:** `Authorization: Bearer {your-admin-token}`

---

## 📋 **Required Fields for Admin Registration**

### **Mandatory Fields:**
- `username` (unique, 3-50 characters)
- `password` (strong password recommended)
- `firstName` (required)
- `lastName` (required)
- `email` (valid email format, unique)

### **Optional Fields:**
- `phoneNumber`

---

## 🔧 **Admin Capabilities After Registration**

Once registered and logged in, admin can access:

### **User Management:**
- `GET /api/admin/mentors` - View all mentors
- `GET /api/admin/sadhakas` - View all sadhakas
- `GET /api/admin/sadhakas/unassigned` - View unassigned sadhakas

### **Assignment Management:**
- `POST /api/admin/assign-sadhaka` - Assign sadhaka to mentor
- `DELETE /api/admin/remove-assignment` - Remove assignment
- `GET /api/admin/assignments` - View assignment overview

---

## 🛡️ **Security Considerations**

### **Password Requirements:**
- Minimum 8 characters
- Include uppercase, lowercase, numbers, special characters
- Example: `HareKrishna123!@#`

### **Production Recommendations:**
1. **Limit Admin Registration:** In production, consider:
   - Requiring existing admin approval
   - Using environment variables for initial admin setup
   - Adding email verification

2. **Secure Admin Access:**
   - Use strong, unique passwords
   - Consider 2FA for admin accounts
   - Monitor admin activity logs

---

## 🎯 **Example Admin Users**

### **Temple Administrator:**
```json
{
  "username": "temple_admin",
  "password": "TempleSecure123!",
  "firstName": "Temple",
  "lastName": "Administrator",
  "email": "admin@newvrindavan.org"
}
```

### **Regional Coordinator:**
```json
{
  "username": "regional_admin",
  "password": "RegionalAdmin456@",
  "firstName": "Regional",
  "lastName": "Coordinator",
  "email": "coordinator@iskcon.org"
}
```

---

## ⚠️ **Important Notes**

### **First Admin Setup:**
- The first admin can be registered without restrictions
- Subsequent admins might require approval (implement as needed)

### **Role Hierarchy:**
- **ADMIN:** Full system access
- **MENTOR:** Limited to assigned sadhakas
- **SADHAKA:** Personal data only

### **Data Storage:**
- Admins are stored in the `sadhakas` table with `role = 'ADMIN'`
- Same authentication mechanism as other users
- Distinguished by role-based access control

---

## 🚀 **Quick Start Command**

For immediate testing, use this curl command:

```bash
curl -X POST http://localhost:8080/api/auth/register/admin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "HareKrishna123!",
    "firstName": "System",
    "lastName": "Admin",
    "email": "admin@iskcon.org"
  }'
```

Your admin registration system is now ready! 🙏
