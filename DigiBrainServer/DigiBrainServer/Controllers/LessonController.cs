using DigiBrainServer.Models;
using DigiBrainServer.ResponseModels;
using DigiBrainServer.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.Controllers
{
    [Route("api/lessons")]
    [ApiController]
    public class LessonController : ControllerBase
    {
        private readonly AppDbContext _context;
        public LessonController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Route("chapter/{chapterId}")]
        public async Task<ActionResult<IEnumerable<LessonResponseModel>>> GetLessonsForChapter(long chapterId)
        {
            var lessons = await _context.Lesson.Where(item => item.ChapterId == chapterId).ToListAsync();

            var responseLessons = new List<LessonResponseModel>();
            foreach (var lesson in lessons)
            {
                responseLessons.Add((LessonResponseModel)lesson);
            }

            return Ok(responseLessons);
        }

        [HttpPost]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<LessonResponseModel>> AddLesson(LessonViewModel lessonModel)
        {
            var lessonCheck = _context.Lesson.Where(item => item.Title.Equals(lessonModel.Title) && item.ChapterId == lessonModel.ChapterId).FirstOrDefault();
            if (lessonCheck != null)
            {
                return BadRequest(new { message = "A lesson with this title already exists" });
            }

            long id = _context.Lesson.Count() + 1;
            lessonCheck = await _context.Lesson.FindAsync(id);
            while (lessonCheck != null)
            {
                id++;
                lessonCheck = await _context.Lesson.FindAsync(id);
            }

            LessonModel lesson = new(id, lessonModel.Title, lessonModel.Text, lessonModel.ChapterId);
            _context.Lesson.Add(lesson);
            await _context.SaveChangesAsync();

            return Ok((LessonResponseModel)lesson);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<LessonResponseModel>> DeleteLesson(long id)
        {
            var lesson = await _context.Lesson.FindAsync(id);
            if (lesson == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This lesson does not exist"
                });
            }

            _context.Lesson.Remove(lesson);
            await _context.SaveChangesAsync();

            return Ok((LessonResponseModel)lesson);
        }

        [HttpPut("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<LessonResponseModel>> UpdateLesson(long id, LessonViewModel lessonModel)
        {
            var lesson = await _context.Lesson.FindAsync(id);
            if (lesson == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This lesson does not exist"
                });
            }

            var lessonCheck = _context.Lesson.Where(item => item.Id != id && item.Title.Equals(lessonModel.Title) && item.ChapterId == lessonModel.ChapterId).FirstOrDefault();
            if (lessonCheck != null)
            {
                return BadRequest(new { message = "A lesson with this title already exists" });
            }

            lesson.Title = lessonModel.Title;
            lesson.Text = lessonModel.Text;
            lesson.ChapterId = lessonModel.ChapterId;

            _context.Lesson.Update(lesson);
            await _context.SaveChangesAsync();

            return Ok((LessonResponseModel)lesson);
        }
    }
}
