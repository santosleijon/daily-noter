interface ErrorAlertProps {
  errorMessage: string,
}

const ErrorAlert = (props: ErrorAlertProps) => {
  if (!props.errorMessage || props.errorMessage.length === 0) {
    return <></>;
  }

  return (
    <div className="px-5 py-2.5 mb-5 me-2 text-sm text-red-800 rounded-lg bg-red-50"
         role="alert">
      <strong>Error:</strong> {props.errorMessage}
    </div>
  )
}

export default ErrorAlert;
