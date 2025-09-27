# Mentor Registration and Login Guide

## 🧑‍🏫 Mentor Registration

### **1. Register a New Mentor**

**Endpoint:** `POST /api/auth/register/mentor`

**Request Body:**
```json
{
  "mentorDTO": {
    "username": "swami_radhanath",
    "firstName": "Radhanath",
    "lastName": "Swami",
    "email": "radhanath.swami@iskcon.org",
    "phoneNumber": "+1234567890",
    "spiritualName": "Radhanath Swami",
    "temple": "Chowpatty Temple",
    "initiationLevel": "Sannyasi",
    "designation": "Sannyasi",
    "yearsOfExperience": 45,
    "specialization": "Bhakti Yoga, Spiritual Guidance"
  },
  "password": "hareKrishna123!"
}
```

**Expected Response (Success):**
```json
{
  "message": "Mentor registration successful",
  "data": {
    "id": 1,
    "username": "swami_radhanath",
    "firstName": "Radhanath",
    "lastName": "Swami",
    "email": "radhanath.swami@iskcon.org",
    "phoneNumber": "+1234567890",
    "spiritualName": "Radhanath Swami",
    "temple": "Chowpatty Temple",
    "initiationLevel": "Sannyasi",
    "designation": "Sannyasi",
    "yearsOfExperience": 45,
    "specialization": "Bhakti Yoga, Spiritual Guidance",
    "role": "MENTOR",
    "active": true,
    "createdAt": "2025-07-16T00:30:00",
    "updatedAt": "2025-07-16T00:30:00"
  }
}
```

---

## 🔐 Mentor Login

### **1. Login with Mentor Credentials**

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "swami_radhanath",
  "password": "hareKrishna123!"
}
```

**Expected Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzd2FtaV9yYWRoYW5hdGgiLCJyb2xlIjoiTUVOVE9SIiwiaWQiOjEsImlhdCI6MTY0MjI5MzEyMCwiZXhwIjoxNjQyMzc5NTIwfQ.xyz...",
  "type": "Bearer",
  "role": "MENTOR",
  "id": 1,
  "username": "swami_radhanath",
  "expiresIn": 86400
}
```

---

## 📝 Required Fields for Mentor Registration

### **Mandatory Fields:**
- `username` (3-50 characters, unique)
- `firstName` (required)
- `lastName` (required)
- `email` (valid email format, unique)
- `password` (strong password recommended)

### **Optional Fields:**
- `phoneNumber`
- `spiritualName`
- `temple`
- `initiationLevel` (e.g., "Brahmachari", "Grihasta", "Sannyasi")
- `designation` (e.g., "Temple President", "Book Distribution Leader")
- `yearsOfExperience`
- `specialization` (e.g., "Japa Guidance", "Book Distribution", "Deity Worship")

---

## 🎯 Postman Testing Steps

### **Step 1: Register Mentor**
1. **Method:** POST
2. **URL:** `http://localhost:8080/api/auth/register/mentor`
3. **Headers:** `Content-Type: application/json`
4. **Body:** Use the JSON example above

### **Step 2: Login as Mentor**
1. **Method:** POST
2. **URL:** `http://localhost:8080/api/auth/login`
3. **Headers:** `Content-Type: application/json`
4. **Body:** Username and password from registration

### **Step 3: Use JWT Token**
Copy the token from login response and use it in other requests:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 🔄 Complete Workflow Example

### **1. Register a Mentor:**
```bash
POST /api/auth/register/mentor
{
  "mentorDTO": {
    "username": "prabhu_radhika",
    "firstName": "Radhika",
    "lastName": "Prabhu",
    "email": "radhika@iskcon.org",
    "temple": "New Vrindavan",
    "designation": "Brahmachari",
    "specialization": "Japa Guidance"
  },
  "password": "jaiRadhe123"
}
```

### **2. Login:**
```bash
POST /api/auth/login
{
  "username": "prabhu_radhika",
  "password": "jaiRadhe123"
}
```

### **3. Access Mentor Features:**
```bash
GET /api/mentor/my-sadhakas
Authorization: Bearer {your-jwt-token}
```

---

## ⚠️ Error Responses

### **Registration Errors:**
```json
{
  "error": "Mentor registration failed",
  "message": "Username already exists: swami_radhanath"
}
```

### **Login Errors:**
```json
{
  "error": "Authentication failed", 
  "message": "Invalid username or password"
}
```

---

## 🎯 Key Differences from Sadhaka Registration

1. **Different Endpoint:** `/api/auth/register/mentor` (vs `/api/auth/register`)
2. **Mentor-Specific Fields:** `designation`, `yearsOfExperience`, `specialization`
3. **Role Assignment:** Automatically set to `MENTOR`
4. **Same Login Endpoint:** Both mentors and sadhakas use `/api/auth/login`

---

## 🚀 Next Steps After Registration

1. **Verify Login:** Test login with new mentor credentials
2. **Access Mentor APIs:** Use mentor-specific endpoints
3. **Assign Sadhakas:** Connect sadhakas to mentors
4. **Provide Feedback:** Start mentoring and giving feedback

The mentor registration system is now fully functional! 🙏
