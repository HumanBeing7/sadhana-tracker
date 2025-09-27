# Sadhana Tracker

A Spring Boot + MySQL application for managing Sadhaka (devotee) registrations, mentor assignments, and ISKCON community data.

---

## 🚀 Features

- Register Sadhakas, Mentors, and Admins via REST API
- Secure authentication and role-based access (JWT)
- Assign Sadhakas to Mentors (admin or mentor self-assignment)
- View, manage, and unassign assignments
- Sample JSON requests for quick testing
- Dockerized MySQL for easy local development

---

## 🏁 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/HumanBeing7/sadhana-tracker.git
cd sadhana-tracker
```

### 2. Start MySQL with Docker

```powershell
docker-compose up -d mysql
```

### 3. Run the Spring Boot Application

```powershell
.\mvnw.cmd spring-boot:run
```

---

## 🗄️ Database Setup

- MySQL runs in Docker (`sadhana-mysql`)
- Database: `sadhana_tracker_db`
- Root password: `password`
- Data persists in Docker volume

See [`DATABASE_SETUP.md`](./DATABASE_SETUP.md) for full details and maintenance commands.

---

## 📝 API Endpoints

### Sadhaka Registration

- **POST** `/api/auth/register`
- See sample JSON files in [`sample-requests/`](./sample-requests):

  - [`register-sadhaka.json`](./sample-requests/register-sadhaka.json): Basic registration
  - [`register-sadhaka-advanced.json`](./sample-requests/register-sadhaka-advanced.json): Advanced devotee
  - [`register-sadhaka-beginner.json`](./sample-requests/register-sadhaka-beginner.json): Beginner devotee

### Mentor Registration

- **POST** `/api/auth/register/mentor`
- See [`MENTOR_REGISTRATION_GUIDE.md`](./MENTOR_REGISTRATION_GUIDE.md) for details

### Admin Registration

- **POST** `/api/auth/register/admin`
- See [`ADMIN_REGISTRATION_GUIDE.md`](./ADMIN_REGISTRATION_GUIDE.md) for details

### Authentication

- **POST** `/api/auth/login` (for all roles)

### Assignment Management

- **POST** `/api/admin/assign-sadhaka` (admin assigns sadhaka to mentor)
- **POST** `/api/mentor/sadhakas/{sadhakaId}/assign` (mentor self-assigns)
- **DELETE** `/api/admin/remove-assignment` (admin removes assignment)
- **GET** `/api/admin/assignments` (view all assignments)

See [`ASSIGNMENT_GUIDE.md`](./ASSIGNMENT_GUIDE.md) and [`ASSIGNMENT_FIX_SUMMARY.md`](./ASSIGNMENT_FIX_SUMMARY.md) for assignment details and fixes.

---

## 🧪 Testing the API

### With cURL

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d @sample-requests/register-sadhaka.json
```

### With Postman

1. Import sample JSON from `sample-requests/`
2. Set endpoint and headers as described above

---

## 📂 Project Structure

- `src/main/java/com/iskon/sadhana/tracker/sadhana_tracker/` — Main application code
- `src/test/java/com/iskon/sadhana/tracker/sadhana_tracker/` — Tests
- `sample-requests/` — Sample JSON files for API testing
- `DATABASE_SETUP.md` — MySQL/Docker setup guide
- `ADMIN_REGISTRATION_GUIDE.md`, `MENTOR_REGISTRATION_GUIDE.md` — Role registration guides
- `ASSIGNMENT_GUIDE.md`, `ASSIGNMENT_FIX_SUMMARY.md`, `MENTOR_CONTROLLER_FIX.md` — Assignment and controller documentation

---

## 🛡️ Security

- Passwords are encrypted
- JWT-based authentication
- Role-based access control (Admin, Mentor, Sadhaka)
- See security notes in the registration guides

---

## 🙏 Credits

Developed for ISKCON community management and mentorship tracking.

---

## 📣 Contributing

Pull requests and suggestions are welcome! Please open an issue for major changes.

---

**Hare Krishna! May your service be successful!**
