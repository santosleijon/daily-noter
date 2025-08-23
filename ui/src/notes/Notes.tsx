import Note from './Note.ts';
import { useEffect, useState } from 'react';
import notesApi from './notesApi.ts';
import ErrorAlert from '../common/ErrorAlert.tsx';
import LoadingSpinner from '../common/LoadingSpinner.tsx';
import NoteComponent from './NoteComponent.tsx';
import LoadableSecondaryButton from '../common/LoadableSecondaryButton.tsx';
import NotesDateWindowBar from './NotesDateWindowBar.tsx';

interface NotesProps {
}

const Notes = (_: NotesProps) => {
  const [notes, setNotes] = useState<Note[]>([]);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const getTodaysDate = (): string => {
    const date = new Date();
    return date.toISOString().split('T')[0];
  };

  const getDateDaysAgo = (initialDate: Date, n: number): string => {
    initialDate.setDate(initialDate.getDate() - Math.abs(n));
    return initialDate.toISOString().split('T')[0];
  };

  const [selectedToDate, setSelectedToDate] = useState<string>(getTodaysDate);
  const [selectedFromDate, setSelectedFromDate] = useState<string>(getDateDaysAgo(new Date(), 4));

  const fetchNotes = async () => {
    const notes = await notesApi.getNotes(selectedFromDate, selectedToDate);
    setNotes(notes);
  };

  useEffect(() => {
    const fetchInitialNotes = async () => {
      try {
        setIsLoading(true);
        await fetchNotes();
      } catch (e) {
        const errorMessage = (e as Error).message;
        setError(errorMessage);
      }
      setIsLoading(false);
    };

    void fetchInitialNotes();
  }, [selectedFromDate, selectedToDate]);

  async function handleSaveNote(note: Note) {
    try {
      await notesApi.updateNote(note);
      await fetchNotes();
    } catch (e) {
      const errorMessage = (e as Error).message;
      setError(errorMessage);
    }
  }

  return <>
    <h2>Your daily notes</h2>

    <NotesDateWindowBar
      selectedFromDate={selectedFromDate}
      selectedToDate={selectedToDate}
      setSelectedFromDate={setSelectedFromDate}
      setSelectedToDate={setSelectedToDate} />

    {isLoading && <LoadingSpinner />}
    <ErrorAlert errorMessage={error} />
    {!error && (
      <>
        {notes.map((note: Note) => <NoteComponent note={note} onSave={handleSaveNote} key={note.noteId} />)}
        {notes.length > 0 && (
          <div className="text-center mt-6">
            <LoadableSecondaryButton
              onClick={() => setSelectedFromDate(getDateDaysAgo(new Date(selectedFromDate), 5))}
              isLoading={isLoading}
              text={`Show older notes`} />
          </div>
        )}
      </>
    )}
  </>;
};

export default Notes;