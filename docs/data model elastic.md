In elasticsearch, there is no dedicated array type. Any field can contain zero or more values by default, however, all values in the array must be of the same datatype.

| PUT /session/{userId} |
| --- |
```json
{
  "mappings":{
    "properties":{
      "userId":{"type":"keyword"},
      "sessionId":{"type":"keyword"},
      "sessionOwner":{"type":"keyword"},
      "participants_limit":{"type":"integer","meta":{"min":1}},
      "sessionState":{
        "type":"nested",
        "properties":{
          "status":{"type":"keyword"},
          "numberOfParticipants":{"type":"integer"},
          "lastQuestionReleased":{"type":"integer"},
          "lastQuestionReleasedDate":{"type":"date","format":"epoch_millis"},
          "endDate":{"type":"date","format":"epoch_millis"}
        }
      },
      "geoLocation":{
        "type":"nested",
        "meta":{"optional":true},
        "properties":{
          "center":{"type":"geo_point"},
          "radius":{"type":"integer"}
        }
      },
    }
  }
}
```
| PUT /quiz/{sessionId} |
| --- |
```json
{
  "mappings":{
    "properties":{
      "userId":{"type":"keyword"},
      "quizId":{"type":"keyword"},
      "sessionId":{"type":"keyword"},
      "quizType":{"type":"keyword"},
      "releaseType":{"type":"keyword"},
      "questions":{
        "type":"nested",
        "meta":{"type":"array"},
        "properties":{
          "questionType":{"type":"keyword"},
          "questionNumber":{"type":"integer"},
          "question":{"type":"text"},
          "options":{
            "type":"nested",
            "meta":{"optional":true,"type":"array"},
            "properties":{
              "option":{"type":"text"}
            }
          },
          "solutionNumber":{"type":"integer","meta":{"optional":true}},
          "solution":{"type":"text","meta":{"optional":true}}
        }
      }
    }
  }
}
```

| PUT /answer/{quizId} |
| --- |
```json
{
  "mappings":{
    "properties":{
      "answerId":{"type":"keyword"},
      "quizId":{"type":"keyword"},
      "sessionId":{"type":"keyword"},
      "guest":{
        "type":"nested",
        "properties":{
          "guestId":{"type":"keyword"},
          "guestIp":{"type":"ip"}
        }
      },
      "answers":{
        "type":"nested",
        "meta":{"type":"array"},
        "properties":{
          "questionNumber":{"type":"integer"},
          "answerType":{"type":"keyword"},
          "answerText":{"type":"text","meta":{"optional":true}},
          "answerNumber":{"type":"integer","meta":{"optional":true}},
          "answerLink":{"type":"text","fields":{"keyword":{"type":"keyword"}},"meta":{"optional":true,"type":"array"}}
        }
      }
    }
  }
}
```

| PUT /users |
| --- |
```json
{
  "mappings":{
    "properties":{
      "userId":{"type":"keyword"},
      "userName":{"type":"keyword"},
      "alias":{"type":"keyword"},
      "registration":{
        "type":"nested",
        "properties":{
          "email":{"type":"text","fields":{"keyword":{"type":"keyword"}}}
        }
      },
    }
  }
}
```
