import Header from './Header.tsx';
import Navigation from './Navigation.tsx';
import Footer from './Footer.tsx';
import { useState } from 'react';
import LoginForm from './users/LoginForm.tsx';
import Note from './notes/Note.ts';
import Notes from './notes/Notes.tsx';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState('');

  function mockApiCallDelay() {
    return new Promise(resolve => setTimeout(resolve, 750));
  }

  async function handleLoginSuccess(email: string) {
    await mockApiCallDelay();
    setIsLoggedIn(true);
    setUserEmail(email);
  }

  async function handleLogoutSuccess() {
    setIsLoggedIn(false);
    setUserEmail('');
  }

  const mockUserId = "1147b4ec-3cbe-4166-9717-ee3992558d91";

  let mockNotes: Note[] = [
    {
      noteId: "b73e65a9-77cd-4c99-97b5-a7935950975a",
      userId: mockUserId,
      date: "2024-01-01",
      content: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
      createdAt: new Date(),
      updatedAt: null
    },
    {
      noteId: "3f0b0613-307c-402c-8604-be1ae1a0eb9f",
      userId: mockUserId,
      date: "2024-01-02",
      content: "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit.",
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      noteId: "4426faea-e44b-4e25-bf5c-03629960f11d",
      userId: mockUserId,
      date: "2024-01-03",
      content: "Magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur",
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  async function handleSaveNote(note: Note) {
    await mockApiCallDelay();
    mockNotes = mockNotes.map(existingNote =>
      existingNote.noteId === note.noteId
        ? { ...existingNote, content: note.content, updatedAt: new Date() }
        : existingNote
    );
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <Navigation isLoggedIn={isLoggedIn} userEmail={userEmail} onLogoutSuccess={handleLogoutSuccess} />
      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          {isLoggedIn ? (
            <Notes notes={mockNotes} onSave={handleSaveNote} />
          ) : (
            <LoginForm onLoginSuccess={handleLoginSuccess} />
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default App;