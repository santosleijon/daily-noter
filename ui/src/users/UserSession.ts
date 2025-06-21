interface UserSession {
  sessionId: string
  userId: string
  userAgent: string
  ipAddress: string
  createdAt: Date
  validTo: Date
  userEmail: string
}

export default UserSession;