# 🏦 XYZ Bank — Online Banking System

A full-stack Online Banking Portal built with Java Spring Boot, MySQL, JWT authentication, and Bootstrap 5. 

## 🛠️ Tech Stack
- **Backend:** Java, Spring Boot 3.x, Spring Security, Spring Data JPA, Hibernate
- **Frontend:** HTML5, CSS3, JavaScript (ES6+), Bootstrap 5
- **Database:** MySQL 8.0
- **Security:** JWT (JSON Web Token) + BCrypt Password Hashing
- **Tools:** IntelliJ IDEA, Postman, MySQL Workbench, Git

## ✨ Features
- User Registration with auto-generated bank account number
- Secure Login with JWT token authentication
- Deposit Money into account
- Withdraw Money with server-side balance validation
- Fund Transfer to another account number
- Real-time Balance Inquiry with last updated timestamp
- Complete Transaction History with color-coded badges

## 🗄️ Database
7 normalized MySQL tables:
`users` `accounts` `transactions` `loans` `loan_emis` `beneficiaries` `audit_logs`

## 📄 Pages
| Page | Description |
|------|-------------|
| welcome.html | Animated splash/loading screen |
| login.html | JWT-based login |
| register.html | New user registration |
| dashboard.html | Profile, balance & quick actions |
| deposit.html | Deposit money |
| withdraw.html | Withdraw money |
| transfer.html | Transfer funds |
| balance.html | Check current balance |
| history.html | Transaction history |
| bank-details.html | Branch information |

## 🚀 How to Run

**Step 1 — Clone the repository**

    git clone https://github.com/Ishu-Harit/xyz-bank-online-banking-system.git

**Step 2 — Create MySQL database**

Open MySQL Workbench and run:

    CREATE DATABASE project;

**Step 3 — Update application.properties**

Open `src/main/resources/application.properties` and set your MySQL credentials:

    spring.datasource.url=jdbc:mysql://localhost:3306/project
    spring.datasource.username=your_username
    spring.datasource.password=your_password

**Step 4 — Run the project**

Open the project in IntelliJ IDEA and run `ProjectApplication.java`

**Step 5 — Open in browser**

    http://localhost:8080/pages/welcome.html

## 👤 Author
**Ishu Harit** — B.Tech ECE, DCRUST Murthal
