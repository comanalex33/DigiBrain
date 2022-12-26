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
    [Route("api/subjects")]
    [ApiController]
    public class SubjectController : ControllerBase
    {
        private readonly AppDbContext _context;

        public SubjectController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        [Route("class/{classId}")]
        public async Task<ActionResult<IEnumerable<SubjectResponseModel>>> GetSubjectForClass(long classId)
        {
            /*var classSubjects = await _context.ClassSubject.Where(item => item.ClassId == classId).ToListAsync();
            var responseSubjects = new List<SubjectResponseModel>();
            foreach(var classSubject in classSubjects)
            {
                var subject = await _context.Subject.FindAsync(classSubject.SubjectId);
                responseSubjects.Add((SubjectResponseModel)subject);
            }*/

            var subjects = await _context.Subject.Where(item => item.ClassId == classId).ToListAsync();
            var responseSubjects = new List<SubjectResponseModel>();
            foreach (var subject in subjects)
            {
                responseSubjects.Add((SubjectResponseModel)subject);
            }

            return Ok(responseSubjects);
        }


        [HttpPost]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<SubjectResponseModel>> AddSubject(SubjectViewModel subjectModel)
        {
            var subjectCheck = _context.Subject.Where(item => item.Name.Equals(subjectModel.Name)).FirstOrDefault();
            if (subjectCheck != null)
            {
                return BadRequest(new { message = "Subject already exists" });
            }
            

            long id = _context.Class.Count() + 1;
            subjectCheck = await _context.Subject.FindAsync(id);
            while (subjectCheck != null)
            {
                id++;
                subjectCheck = await _context.Subject.FindAsync(id);
            }

            SubjectModel subject = new(id, subjectModel.Name, subjectModel.ClassId, subjectModel.IconId);
            _context.Subject.Add(subject);
            await _context.SaveChangesAsync();

            return Ok((SubjectResponseModel)subject);
        }

        [HttpDelete("{id}")]
        [Authorize(Roles = "admin")]
        public async Task<ActionResult<SubjectResponseModel>> DeleteSubject(long id)
        {
            var subject = await _context.Subject.FindAsync(id);
            if (subject == null)
            {
                return NotFound(new ErrorResponseModel
                {
                    Message = "This subject does not exist"
                });
            }

            _context.Subject.Remove(subject);
            await _context.SaveChangesAsync();

            return Ok((SubjectResponseModel)subject);
        }
    }
}
