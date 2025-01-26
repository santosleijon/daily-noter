import Note from './Note.ts';

interface NotesProps {
  notes: Note[];
}

const Notes = (props: NotesProps) => {
  return <>
    <h2>Your daily notes</h2>
    {props.notes.map((note: Note) =>
      <div className="p-6 pt-5 mb-8 md:flex-row md:gap-8 rounded-xl bg-cyan-100">
        <div className="flex items-center">
          <h3>{note.date}</h3>
          <div className="text-xs ml-3 mb-1 text-cyan-800">
            Skapad {note.createdAt.toISOString()}{note.updatedAt && <>, uppdaterad {note.updatedAt.toISOString()}</>}
          </div>
        </div>
        <p>{note.content}</p>
      </div>
    )}
  </>;
}

export default Notes;