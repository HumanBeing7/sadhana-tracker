# Sadhaka-Mentor Assignment Guide

## 🎯 **Assignment Methods Available**

### **1. Mentor Self-Assignment (Existing)**
**Endpoint:** `POST /api/mentor/sadhakas/{sadhakaId}/assign`
- Mentors can assign sadhakas to themselves
- Requires mentor JWT token

### **2. Admin Assignment (New)**
**Endpoint:** `POST /api/admin/assign-sadhaka`
- Admins can assign any sadhaka to any mentor
- Requires admin JWT token

---

## 🔐 **Admin Assignment (Recommended)**

### **Assign Sadhaka to Mentor**

**Request:**
```http
POST /api/admin/assign-sadhaka
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json

{
  "mentorId": 1,
  "sadhakaId": 2
}
```

**Response (Success):**
```json
{
  "message": "Sadhaka assigned to mentor successfully",
  "data": null
}
```

### **Remove Assignment**

**Request:**
```http
DELETE /api/admin/remove-assignment
Authorization: Bearer {admin-jwt-token}
Content-Type: application/json

{
  "mentorId": 1,
  "sadhakaId": 2
}
```

**Response (Success):**
```json
{
  "message": "Sadhaka removed from mentor successfully",
  "data": null
}
```

---

## 📋 **Admin Management Endpoints**

### **1. Get All Mentors**
```http
GET /api/admin/mentors
Authorization: Bearer {admin-jwt-token}
```

**Response:**
```json
[
  {
    "id": 1,
    "username": "swami_radhanath",
    "firstName": "Radhanath",
    "lastName": "Swami",
    "email": "radhanath@iskcon.org",
    "temple": "Chowpatty",
    "designation": "Sannyasi",
    "specialization": "Bhakti Yoga",
    "active": true
  }
]
```

### **2. Get All Sadhakas**
```http
GET /api/admin/sadhakas
Authorization: Bearer {admin-jwt-token}
```

### **3. Get Unassigned Sadhakas**
```http
GET /api/admin/sadhakas/unassigned
Authorization: Bearer {admin-jwt-token}
```

**Response:**
```json
[
  {
    "id": 2,
    "username": "arjuna_devotee",
    "firstName": "Arjuna",
    "lastName": "Das",
    "email": "arjuna@example.com",
    "temple": "New Vrindavan",
    "active": true
  }
]
```

### **4. Get Assignment Overview**
```http
GET /api/admin/assignments
Authorization: Bearer {admin-jwt-token}
```

**Response:**
```json
[
  {
    "mentorId": 1,
    "mentorName": "Radhanath Swami",
    "mentorEmail": "radhanath@iskcon.org",
    "assignedSadhakas": 3,
    "sadhakaNames": ["Arjuna Das", "Krishna Prabhu", "Radha Devi"]
  }
]
```

---

## 🧑‍🏫 **Mentor Self-Assignment**

### **Assign Sadhaka to Self**
```http
POST /api/mentor/sadhakas/{sadhakaId}/assign
Authorization: Bearer {mentor-jwt-token}
```

### **Remove Sadhaka from Self**
```http
DELETE /api/mentor/sadhakas/{sadhakaId}
Authorization: Bearer {mentor-jwt-token}
```

---

## 🔍 **How to Create an Admin User**

Since you need admin privileges for assignment, you need to create an admin user. You can either:

### **Option 1: Database Insert**
```sql
INSERT INTO sadhakas (username, password, first_name, last_name, email, role, active, created_at, updated_at) 
VALUES ('admin', '$2a$10$encodedPassword', 'System', 'Administrator', 'admin@iskcon.org', 'ADMIN', true, NOW(), NOW());
```

### **Option 2: Add Admin Registration Endpoint**
Create `/api/auth/register/admin` similar to mentor registration.

---

## 🎯 **Complete Assignment Workflow**

### **Step 1: Login as Admin**
```http
POST /api/auth/login
{
  "username": "admin",
  "password": "adminPassword"
}
```

### **Step 2: Get Available Mentors**
```http
GET /api/admin/mentors
Authorization: Bearer {admin-token}
```

### **Step 3: Get Unassigned Sadhakas**
```http
GET /api/admin/sadhakas/unassigned
Authorization: Bearer {admin-token}
```

### **Step 4: Assign Sadhaka to Mentor**
```http
POST /api/admin/assign-sadhaka
Authorization: Bearer {admin-token}
{
  "mentorId": 1,
  "sadhakaId": 2
}
```

### **Step 5: Verify Assignment**
```http
GET /api/admin/assignments
Authorization: Bearer {admin-token}
```

---

## 📊 **Mentor View of Assignments**

### **Check My Assigned Sadhakas**
```http
GET /api/mentor/sadhakas
Authorization: Bearer {mentor-token}
```

### **Get Sadhaka Reports**
```http
GET /api/mentor/sadhakas/{sadhakaId}/reports?days=7
Authorization: Bearer {mentor-token}
```

### **Get Sadhaka Statistics**
```http
GET /api/mentor/sadhakas/{sadhakaId}/statistics?days=30
Authorization: Bearer {mentor-token}
```

---

## ⚠️ **Security & Permissions**

### **Role Requirements:**
- **Admin Assignment:** Requires `ADMIN` role
- **Mentor Self-Assignment:** Requires `MENTOR` role
- **Viewing Assignments:** Each role can only see their own data

### **Access Control:**
- Mentors can only access sadhakas assigned to them
- Admins can access all assignments
- Sadhakas cannot assign themselves

---

## 🚀 **Testing in Postman**

### **Collection Structure:**
```
📁 Admin Assignment
├── 🔐 Login as Admin
├── 👥 Get All Mentors
├── 👤 Get Unassigned Sadhakas
├── ➕ Assign Sadhaka to Mentor
├── 📊 View Assignments Overview
└── ➖ Remove Assignment

📁 Mentor Assignment
├── 🔐 Login as Mentor
├── 👥 Get My Sadhakas
├── ➕ Assign Sadhaka to Self
└── ➖ Remove Sadhaka from Self
```

The system now provides comprehensive assignment functionality for both admin-managed and mentor-managed assignments! 🙏
