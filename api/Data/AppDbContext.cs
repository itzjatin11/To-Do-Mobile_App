using Microsoft.EntityFrameworkCore;
using api.Models;


namespace api.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<Lecture> Lectures { get; set; }
        public DbSet<Meeting> Meetings { get; set; }

    }
}
