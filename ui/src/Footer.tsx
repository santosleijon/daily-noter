const Footer = () => {
  return <footer className="bg-gray-800 text-white">
    <div className="container mx-auto px-4 py-6 text-center">
      <p>&copy; {new Date().getFullYear()} Santos Leijon &middot; <a href={"https://github.com/santosleijon/daily-noter"}>github.com/santosleijon/daily-noter</a></p>
    </div>
  </footer>
}

export default Footer;
