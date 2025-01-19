import Header from './Header.tsx';
import Navigation from './Navigation.tsx';
import Footer from './Footer.tsx';

const App = () => {
  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <Navigation />

      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-2xl font-semibold mb-4">Welcome to Daily Noter</h2>
          <p className="text-gray-600">This is where you'll be writing your daily notes...</p>
        </div>
      </main>

      <Footer />
    </div>
  )
}

export default App