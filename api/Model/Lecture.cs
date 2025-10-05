using System.ComponentModel.DataAnnotations;

namespace api.Models
{
    public class Lecture
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string Topic { get; set; }

        [Required]
        public string Date { get; set; }  // You can change this to DateTime if needed

        [Required]
        public string Time { get; set; }  // You can also use TimeSpan

        public string Notes { get; set; }
    }
}
