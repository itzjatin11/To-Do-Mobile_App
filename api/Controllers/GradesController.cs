using api.Models;
using Google.Cloud.Firestore;
using Google.Apis.Auth.OAuth2;
using Microsoft.AspNetCore.Mvc;
using Google.Cloud.Firestore.V1;

namespace api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class GradesController : ControllerBase
    {
        private readonly FirestoreDb _db;

        public GradesController()
        {
            // ✅ Initialize Firestore with credentials from firebase-adminsdk.json
            var credential = GoogleCredential.FromFile("firebase-adminsdk.json");
            var clientBuilder = new FirestoreClientBuilder
            {
                Credential = credential
            };
            var firestoreClient = clientBuilder.Build();
            _db = FirestoreDb.Create("todoapp-c8423", firestoreClient);
        }

        // 🚀 GET: api/grades/user/{username}
        [HttpGet("user/{username}")]
        public async Task<ActionResult<IEnumerable<Grade>>> GetGradesByUser(string username)
        {
            try
            {
                var query = _db.Collection("grades").WhereEqualTo("username", username);
                var snapshot = await query.GetSnapshotAsync();

                var grades = new List<Grade>();
                foreach (var doc in snapshot.Documents)
                {
                    grades.Add(new Grade
                    {
                        Id = doc.Id,
                        Username = doc.ContainsField("username") ? doc.GetValue<string>("username") : "",
                        Course = doc.ContainsField("course") ? doc.GetValue<string>("course") : "",
                        Type = doc.ContainsField("type") ? doc.GetValue<string>("type") : "",
                        Status = doc.ContainsField("status") ? doc.GetValue<string>("status") : "",
                        DueDate = doc.ContainsField("due_date") ? doc.GetValue<string>("due_date") : ""
                    });

                }

                return Ok(grades);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Error fetching grades: {ex.Message}");
            }
        }

        // ➕ POST: api/grades
        [HttpPost]
        public async Task<ActionResult> AddGrade([FromBody] Grade grade)
        {
            try
            {
                var data = new Dictionary<string, object>
                {
                    { "username", grade.Username },
                    { "course", grade.Course },
                    { "type", grade.Type },
                    { "status", grade.Status },
                    { "due_date", grade.DueDate }
                };

                var result = await _db.Collection("grades").AddAsync(data);
                return Ok(new { message = "Grade added", id = result.Id });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Error adding grade: {ex.Message}");
            }
        }

        // ✏️ PUT: api/grades/{id}
        [HttpPut("{id}")]
        public async Task<ActionResult> UpdateGrade(string id, [FromBody] Grade grade)
        {
            try
            {
                var docRef = _db.Collection("grades").Document(id);
                await docRef.SetAsync(new Dictionary<string, object>
                {
                    { "username", grade.Username },
                    { "course", grade.Course },
                    { "type", grade.Type },
                    { "status", grade.Status },
                    { "due_date", grade.DueDate }
                });

                return Ok(new { message = "Grade updated" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Error updating grade: {ex.Message}");
            }
        }

        // ❌ DELETE: api/grades/{id}
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteGrade(string id)
        {
            try
            {
                await _db.Collection("grades").Document(id).DeleteAsync();
                return Ok(new { message = "Grade deleted" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Error deleting grade: {ex.Message}");
            }
        }
    }
}
