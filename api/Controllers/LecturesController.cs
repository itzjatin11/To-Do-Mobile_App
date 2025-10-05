using Microsoft.AspNetCore.Mvc;
using api.Data;
using api.Models;
using Microsoft.EntityFrameworkCore;

namespace api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class LecturesController : ControllerBase
    {
        private readonly AppDbContext _context;

        public LecturesController(AppDbContext context)
        {
            _context = context;
        }

        // GET: api/Lectures
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Lecture>>> GetAll()
        {
            return await _context.Lectures.ToListAsync();
        }

        // POST: api/Lectures
        [HttpPost]
        public async Task<ActionResult<Lecture>> AddLecture([FromBody] Lecture lecture)
        {
            _context.Lectures.Add(lecture);
            await _context.SaveChangesAsync();

            // Return 201 Created with location header
            return CreatedAtAction(nameof(GetLectureById), new { id = lecture.Id }, lecture);
        }

        // GET: api/Lectures/5
        [HttpGet("{id}")]
        public async Task<ActionResult<Lecture>> GetLectureById(int id)
        {
            var lecture = await _context.Lectures.FindAsync(id);
            if (lecture == null) return NotFound();
            return lecture;
        }

        // PUT: api/Lectures/5
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateLecture(int id, [FromBody] Lecture lecture)
        {
            if (id != lecture.Id)
            {
                return BadRequest("ID in URL and body do not match");
            }

            var existing = await _context.Lectures.FindAsync(id);
            if (existing == null) return NotFound();

            existing.Topic = lecture.Topic;
            existing.Date = lecture.Date;
            existing.Time = lecture.Time;
            existing.Notes = lecture.Notes;

            await _context.SaveChangesAsync();
            return Ok(existing);
        }

        // DELETE: api/Lectures/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteLecture(int id)
        {
            var existing = await _context.Lectures.FindAsync(id);
            if (existing == null) return NotFound();

            _context.Lectures.Remove(existing);
            await _context.SaveChangesAsync();
            return Ok(new { message = "Lecture deleted" });
        }
    }
}
