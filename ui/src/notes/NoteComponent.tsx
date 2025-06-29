import Note from './Note.js';
import React, { useState } from 'react';
import LoadableSubmitButton from '../common/LoadableSubmitButton.js';

interface NoteComponentProps {
  note: Note;
  onSave: (note: Note) => Promise<void>;
  key: string
}

const NoteComponent = ({ note, onSave }: NoteComponentProps) => {
  const [content, setContent] = useState(note.content);
  const [saveIsLoading, setSaveIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaveIsLoading(true);
    await onSave({...note, content: content});
    setSaveIsLoading(false);
  };

  function getWeekdayFromDateString(dateString: string): string {
    const date = new Date(dateString);

    if (isNaN(date.getTime())) {
      throw Error("Invalid date: " + dateString);
    }

    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
    };

    const formatter = new Intl.DateTimeFormat('en-US', options);

    return formatter.format(date);
  }

  function getPrettyDate(date: Date): string {
    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hourCycle: 'h23'
    };

    const formatter = new Intl.DateTimeFormat('sv-SE', options);

    return formatter.format(date);
  }

  const noteDate = `${getWeekdayFromDateString(note.date)}, ${note.date}`;

  return (
    <div>
      <div>
        <h3 className="mt-8">{noteDate}</h3>
      </div>
      <div className="text-xs mb-2 text-cyan-800">
        <p>Created {getPrettyDate(note.createdAt)} {note.updatedAt &&
          <>(updated {getPrettyDate(note.updatedAt)})</>}</p>
      </div>
      <form onSubmit={handleSubmit}>
        <textarea
          className="block mb-3 p-2 w-full text-sm text-gray-900 rounded border-solid field-sizing-content overflow-hidden"
          rows={10}
          onChange={(e) => setContent(e.target.value)}
          defaultValue={note.content} />
        <LoadableSubmitButton isLoading={saveIsLoading} text="Save" />
      </form>
    </div>
  )
}

export default NoteComponent;