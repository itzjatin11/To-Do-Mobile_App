using System.ComponentModel.DataAnnotations;

namespace api.Models
{
    public class Meeting
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string Title { get; set; }

        [Required]
        public string Date { get; set; } // You can change to DateTime if you want strict type

        [Required]
        public string Time { get; set; }

        public string Purpose { get; set; }

        public string Attendees { get; set; }

        [Required]
        public string Username { get; set; }
    }
}
