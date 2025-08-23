interface NotesDateWindowBarProps {
  selectedFromDate: string,
  selectedToDate: string,
  setSelectedFromDate: (date: string) => void,
  setSelectedToDate: (date: string) => void,
}

const NotesDateWindowBar = (props: NotesDateWindowBarProps) => {
  return (
    <div className="container mx-auto px-4 py-3 mb-2 flex items-center border-solid border-t border-b border-gray-500">
      <div className="flex-grow">
        <p>Showing notes from
          <input
            type="date"
            id="start"
            name="notes-to-from-input"
            className="mx-2"
            value={props.selectedFromDate}
            min="1900-01-01"
            max="2099-12-31"
            onChange={(e) => props.setSelectedFromDate(e.target.value)}
          />
          to
          <input
            type="date"
            id="start"
            name="notes-to-date-input"
            className="mx-2"
            value={props.selectedToDate}
            min="1900-01-01"
            max="2099-12-31"
            onChange={(e) => props.setSelectedToDate(e.target.value)}
          />
        </p>
      </div>
    </div>
  );
}

export default NotesDateWindowBar;