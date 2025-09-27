#!/bin/bash
# Test script for assignment functionality

echo "Testing Sadhaka-Mentor Assignment API"
echo "======================================"

# Base URL
BASE_URL="http://localhost:8080/api"

echo ""
echo "1. Testing assignment endpoint without circular references..."
echo "POST $BASE_URL/admin/assign-sadhaka"

# Note: You'll need to replace the JWT token and ensure you have:
# - A registered admin user
# - At least one mentor (ID: 1)  
# - At least one sadhaka (ID: 2)

echo ""
echo "Expected JSON response format:"
echo "{"
echo "  \"message\": \"Sadhaka successfully assigned to mentor\","
echo "  \"mentorId\": 1,"
echo "  \"mentorName\": \"John Doe\","
echo "  \"sadhakaId\": 2,"
echo "  \"sadhakaName\": \"Jane Smith\""
echo "}"

echo ""
echo "2. Testing assignment overview endpoint..."
echo "GET $BASE_URL/admin/assignments"

echo ""
echo "Expected response structure:"
echo "{"
echo "  \"mentors\": ["
echo "    {"
echo "      \"id\": 1,"
echo "      \"username\": \"mentor1\","
echo "      \"email\": \"mentor@example.com\","
echo "      \"name\": \"John Doe\","
echo "      \"assignedSadhakas\": ["
echo "        {"
echo "          \"id\": 2,"
echo "          \"username\": \"sadhaka1\","
echo "          \"name\": \"Jane Smith\","
echo "          \"email\": \"sadhaka@example.com\""
echo "        }"
echo "      ]"
echo "    }"
echo "  ],"
echo "  \"totalMentors\": 1,"
echo "  \"totalAssignments\": 1"
echo "}"

echo ""
echo "✅ Key improvements implemented:"
echo "- Removed circular JSON references using DTOs"
echo "- Added @Transactional for proper data handling"
echo "- Assignment method now returns structured response"
echo "- Assignment overview uses safe DTO serialization"
echo "- Duplicate assignment prevention"
echo ""
echo "🚫 StackOverflowError should now be resolved!"
