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
    [Route("api/chapters")]
    [ApiController]
    public class ChapterController : ControllerBase
    {
        private readonly AppDbContext _context;
        public ChapterController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Route("subject/{subjectId}")]
        public async Task<ActionResult<IEnumerable<ChapterResponseModel>>> GetChaptersForSubject(long subjectId)
        {
            var chapters = await _context.Chapter.Where(item => item.SubjectId == subjectId).ToListAsync();

            var responseChapters = new List<ChapterResponseModel>();
            foreach (var chapter in chapters)
            {
                responseChapters.Add((ChapterResponseModel)chapter);
            }

            return Ok(responseChapters);
        }

        [HttpPost]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<ChapterResponseModel>> AddChapter(ChapterViewModel chapterModel)
        {
            var chapterCheck = _context.Chapter.Where(item => item.Number == chapterModel.Number && item.SubjectId == chapterModel.SubjectId).FirstOrDefault();
            if (chapterCheck != null)
            {
                return BadRequest(new { message = "A chapter with this number already exists" });
            }

            chapterCheck = _context.Chapter.Where(item => item.Name.Equals(chapterModel.Name) && item.SubjectId == chapterModel.SubjectId).FirstOrDefault();
            if (chapterCheck != null)
            {
                return BadRequest(new { message = "A chapter with this name already exists" });
            }

            long id = _context.Chapter.Count() + 1;
            chapterCheck = await _context.Chapter.FindAsync(id);
            while (chapterCheck != null)
            {
                id++;
                chapterCheck = await _context.Chapter.FindAsync(id);
            }

            ChapterModel chapter = new(id, chapterModel.Name, chapterModel.Number, chapterModel.SubjectId);
            _context.Chapter.Add(chapter);
            await _context.SaveChangesAsync();

            return Ok((ChapterResponseModel)chapter);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<ChapterResponseModel>> DeleteChapter(long id)
        {
            var chapter = await _context.Chapter.FindAsync(id);
            if (chapter == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This chapter does not exist"
                });
            }

            _context.Chapter.Remove(chapter);
            await _context.SaveChangesAsync();

            return Ok((ChapterResponseModel)chapter);
        }

        [HttpPut("{id}")]
        [Authorize(Roles = "admin,teacher")]
        public async Task<ActionResult<ChapterResponseModel>> UpdateChapter(long id, ChapterViewModel chapterModel)
        {
            var chapter = await _context.Chapter.FindAsync(id);
            if (chapter == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This chapter does not exist"
                });
            }

            var chapterCheck = _context.Chapter.Where(item => item.Id != chapter.Id && item.Number == chapterModel.Number && item.SubjectId == chapterModel.SubjectId).FirstOrDefault();
            if (chapterCheck != null)
            {
                return BadRequest(new { message = "A chapter with this number already exists" });
            }

            chapterCheck = _context.Chapter.Where(item => item.Id != chapter.Id && item.Name.Equals(chapterModel.Name) && item.SubjectId == chapterModel.SubjectId).FirstOrDefault();
            if (chapterCheck != null)
            {
                return BadRequest(new { message = "A chapter with this name already exists" });
            }

            chapter.Name = chapterModel.Name;
            chapter.Number = chapterModel.Number;
            chapter.SubjectId = chapterModel.SubjectId;

            _context.Chapter.Update(chapter);
            await _context.SaveChangesAsync();

            return Ok((ChapterResponseModel)chapter);
        }
    }
}
