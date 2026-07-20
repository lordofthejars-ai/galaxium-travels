import { useState, useRef, useEffect } from 'react';
import type { ReactNode } from 'react';
import { MessageCircle, X, Send, HelpCircle } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import ReactMarkdown from 'react-markdown';
import { sendChatMessage, getChatbotSuggestedQuestions } from '../../services/api';
import type { SuggestedQuestion } from '../../services/api';
import { useUser } from '../../hooks/useUser';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
}

export const ChatBot = () => {
  const { user } = useUser();
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: 'Hello! I\'m your Galaxium assistant. How can I help you with your interplanetary travel today?',
      sender: 'bot',
      timestamp: new Date(),
    },
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [suggestedQuestions, setSuggestedQuestions] = useState<SuggestedQuestion[]>([]);
  const [isSuggestionsLoading, setIsSuggestionsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const suggestionsRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Close suggestions dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (suggestionsRef.current && !suggestionsRef.current.contains(e.target as Node)) {
        setShowSuggestions(false);
      }
    };
    if (showSuggestions) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showSuggestions]);

  const handleToggleSuggestions = async () => {
    if (showSuggestions) {
      setShowSuggestions(false);
      return;
    }

    setShowSuggestions(true);

    // Fetch only if we don't have questions yet
    if (suggestedQuestions.length === 0) {
      setIsSuggestionsLoading(true);
      try {
        const questions = await getChatbotSuggestedQuestions();
        setSuggestedQuestions(questions);
      } catch {
        setSuggestedQuestions([]);
      } finally {
        setIsSuggestionsLoading(false);
      }
    }
  };

  const handleSelectQuestion = (question: SuggestedQuestion) => {
    setInputValue(question.text);
    setShowSuggestions(false);
  };

  const handleSendMessage = async () => {
    if (!inputValue.trim() || isLoading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputValue,
      sender: 'user',
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);

    try {
      // Send message to chatbot endpoint with userId
      if (!user?.user_id) {
        throw new Error('User not authenticated');
      }

      const response = await sendChatMessage(user.user_id, inputValue);

      // Handle different response formats
      let responseText: string;
      if (typeof response === 'string') {
        // Backend returned plain text
        responseText = response;
      } else if (response && typeof response === 'object' && 'response' in response) {
        // Backend returned { response: "text" }
        responseText = response.response;
      } else {
        // Fallback
        responseText = 'I apologize, but I couldn\'t process that request.';
      }

      const botMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: responseText,
        sender: 'bot',
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, botMessage]);
    } catch (error) {
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: 'I\'m having trouble connecting right now. Please try again later.',
        sender: 'bot',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <>
      {/* Floating Chat Button */}
      <motion.button
        initial={{ scale: 0 }}
        animate={{ scale: 1 }}
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.9 }}
        onClick={() => setIsOpen(!isOpen)}
        className="fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full bg-cosmic-gradient shadow-lg shadow-cosmic-purple/50 flex items-center justify-center text-white hover:shadow-xl hover:shadow-cosmic-purple/70 transition-all duration-300"
        aria-label="Open chat"
      >
        <AnimatePresence mode="wait">
          {isOpen ? (
            <motion.div
              key="close"
              initial={{ rotate: -90, opacity: 0 }}
              animate={{ rotate: 0, opacity: 1 }}
              exit={{ rotate: 90, opacity: 0 }}
              transition={{ duration: 0.2 }}
            >
              <X size={24} />
            </motion.div>
          ) : (
            <motion.div
              key="open"
              initial={{ rotate: 90, opacity: 0 }}
              animate={{ rotate: 0, opacity: 1 }}
              exit={{ rotate: -90, opacity: 0 }}
              transition={{ duration: 0.2 }}
            >
              <MessageCircle size={24} />
            </motion.div>
          )}
        </AnimatePresence>
      </motion.button>

      {/* Chat Window */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            transition={{ duration: 0.2 }}
            className="fixed bottom-24 right-6 z-50 w-96 h-[500px] glass-card flex flex-col shadow-2xl"
          >
            {/* Header */}
            <div className="bg-cosmic-gradient p-4 rounded-t-xl flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-white/20 flex items-center justify-center">
                  <MessageCircle size={20} className="text-white" />
                </div>
                <div>
                  <h3 className="text-white font-semibold">Galaxium Assistant</h3>
                  <p className="text-white/80 text-xs">Always here to help</p>
                </div>
              </div>
            </div>

            {/* Messages */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {messages.map((message) => (
                <motion.div
                  key={message.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className={`flex ${message.sender === 'user' ? 'justify-end' : 'justify-start'}`}
                >
                  <div
                    className={`max-w-[80%] rounded-lg p-3 ${
                      message.sender === 'user'
                        ? 'bg-cosmic-gradient text-white'
                        : 'bg-white/10 text-star-white'
                    }`}
                  >
                    {message.sender === 'bot' ? (
                      <div className="text-sm prose prose-invert prose-sm max-w-none">
                        <ReactMarkdown
                          components={{
                            p: ({ children }: { children: ReactNode }) => <p className="mb-2 last:mb-0">{children}</p>,
                            ul: ({ children }: { children: ReactNode }) => <ul className="list-disc list-inside mb-2 last:mb-0">{children}</ul>,
                            ol: ({ children }: { children: ReactNode }) => <ol className="list-decimal list-inside mb-2 last:mb-0">{children}</ol>,
                            li: ({ children }: { children: ReactNode }) => <li className="mb-1">{children}</li>,
                            strong: ({ children }: { children: ReactNode }) => <strong className="font-semibold">{children}</strong>,
                            em: ({ children }: { children: ReactNode }) => <em className="italic">{children}</em>,
                            code: ({ children }: { children: ReactNode }) => <code className="bg-white/10 px-1 py-0.5 rounded text-xs">{children}</code>,
                            h1: ({ children }: { children: ReactNode }) => <h1 className="text-lg font-bold mb-2">{children}</h1>,
                            h2: ({ children }: { children: ReactNode }) => <h2 className="text-base font-bold mb-2">{children}</h2>,
                            h3: ({ children }: { children: ReactNode }) => <h3 className="text-sm font-bold mb-1">{children}</h3>,
                          }}
                        >
                          {message.text}
                        </ReactMarkdown>
                      </div>
                    ) : (
                      <p className="text-sm">{message.text}</p>
                    )}
                    <p className="text-xs opacity-60 mt-1">
                      {message.timestamp.toLocaleTimeString([], {
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </p>
                  </div>
                </motion.div>
              ))}
              {isLoading && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="flex justify-start"
                >
                  <div className="bg-white/10 rounded-lg p-3">
                    <div className="flex gap-1">
                      <div className="w-2 h-2 bg-star-white rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                      <div className="w-2 h-2 bg-star-white rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                      <div className="w-2 h-2 bg-star-white rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                    </div>
                  </div>
                </motion.div>
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Input */}
            <div className="p-4 border-t border-white/10">
              {/* Suggested Questions Dropdown */}
              <div className="relative" ref={suggestionsRef}>
                <AnimatePresence>
                  {showSuggestions && (
                    <motion.div
                      initial={{ opacity: 0, y: 6 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: 6 }}
                      transition={{ duration: 0.15 }}
                      className="absolute bottom-full mb-2 left-0 right-0 bg-deep-space border border-white/15 rounded-lg overflow-hidden shadow-xl"
                    >
                      <p className="px-3 py-2 text-xs text-gray-400 border-b border-white/10">
                        Suggested questions
                      </p>
                      {isSuggestionsLoading ? (
                        <div className="flex items-center justify-center py-4 gap-1">
                          <div className="w-1.5 h-1.5 bg-star-white rounded-full animate-bounce" style={{ animationDelay: '0ms' }} />
                          <div className="w-1.5 h-1.5 bg-star-white rounded-full animate-bounce" style={{ animationDelay: '150ms' }} />
                          <div className="w-1.5 h-1.5 bg-star-white rounded-full animate-bounce" style={{ animationDelay: '300ms' }} />
                        </div>
                      ) : suggestedQuestions.length === 0 ? (
                        <p className="px-3 py-3 text-sm text-gray-400 text-center">
                          No suggestions available
                        </p>
                      ) : (
                        <ul className="max-h-48 overflow-y-auto">
                          {suggestedQuestions.map((q) => (
                            <li key={q.id}>
                              <button
                                onClick={() => handleSelectQuestion(q)}
                                className="w-full text-left px-3 py-2.5 text-sm text-star-white hover:bg-white/10 transition-colors duration-150"
                              >
                                {q.text}
                              </button>
                            </li>
                          ))}
                        </ul>
                      )}
                    </motion.div>
                  )}
                </AnimatePresence>

                <div className="flex gap-2">
                  <button
                    onClick={handleToggleSuggestions}
                    aria-label="Show suggested questions"
                    title="Suggested questions"
                    className={`p-2 rounded-lg border transition-all duration-200 ${
                      showSuggestions
                        ? 'bg-cosmic-gradient border-transparent text-white'
                        : 'bg-white/5 border-white/10 text-gray-400 hover:text-star-white hover:bg-white/10'
                    }`}
                  >
                    <HelpCircle size={20} />
                  </button>
                  <input
                    type="text"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder="Type your message..."
                    disabled={isLoading}
                    className="flex-1 bg-white/5 border border-white/10 rounded-lg px-4 py-2 text-star-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-cosmic-purple focus:border-transparent transition-all duration-200 disabled:opacity-50"
                  />
                  <button
                    onClick={handleSendMessage}
                    disabled={!inputValue.trim() || isLoading}
                    className="bg-cosmic-gradient text-white p-2 rounded-lg hover:shadow-lg hover:shadow-cosmic-purple/50 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
                    aria-label="Send message"
                  >
                    <Send size={20} />
                  </button>
                </div>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
};

// Made with Bob
