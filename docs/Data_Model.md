| SESSION |
| --- |
```json

{
	"Session_Id": String,
	"Session_Owner": String,
    "Participants_Limit": Int,
    "Session_Date": String,
	"Session_State": {
        "Status": String,
        "Release_Type": String,
        "Number_Participants": Int,
        "Geolocation": String,
        "Last_Question_Released": Int,
        "Last_Question_Released_Date": String,
        "End_Date": String
    },
    "Quiz": {
        "Type": String,
        "Template_Id": String,
        "Questions": [
            {
                "Question_Type": String,
                "Question_Number": Int,
                "Question": String,
                "Options": [
                    { 
                        "Option": String
                    }
                ],
                "Solution_Number": Int,
                "Solution": String
            }
        ],
        "Answers": [
            {
                "Guest_Id": String,
                "Question_Number": Int,
                "Answer_Type": String,
                "Answer_Text": String,
                "Answer_Number": Int,
                "Answer_Link": String
            }
        ]
    }
}
```

| USER |
| --- |
```json
{
    "User_Id": String,
    "User_Name": String,
    "Alias": String,
    "Login_Type": String,
    "GitHub": ObjectId,
    "Google": ObjectId,
    "Local": {
        "Salt": String,
        "Hash": String
    },
    "Session_History": [
        {
            "Session_Id": String 
        }
    ],
    "Templates": [
        {
            "Template_Id": String
        }
    ]
}
```

| QUIZ TEMPLATE |
| --- |
```json
{
    "Template_Id": String,
    "Template_Alias": String,
    "Questions": [
        {
            "Question_Type": String,
            "Question_Number": Int,
            "Question": String,
            "Options": [
                { 
                    "Option": String
                }
            ],
            "Solution_Number": Int,
            "Solution": String
        }
    ],
}
```