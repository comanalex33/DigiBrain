using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace DigiBrainServer.ResponseModels
{
    public class PathLessonResponseModel
    {
        public long Id { get; set; }
        public long Number { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public long PathSectionId { get; set; }
    }
}
