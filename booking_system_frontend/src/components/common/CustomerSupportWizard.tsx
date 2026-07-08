import { useState, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Upload, CheckCircle, X, ChevronRight, Rocket } from 'lucide-react';
import { Modal } from './Modal';
import { Button } from './Button';
import { scanTicket, storeTicket } from '../../services/api';
import type { TicketScanResponse } from '../../services/api';

interface CustomerSupportWizardProps {
  isOpen: boolean;
  onClose: () => void;
}

type Step = 1 | 2 | 3;

export const CustomerSupportWizard = ({ isOpen, onClose }: CustomerSupportWizardProps) => {
  const [step, setStep] = useState<Step>(1);
  const [file, setFile] = useState<File | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [scanResult, setScanResult] = useState<TicketScanResponse | null>(null);
  const [message, setMessage] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);

  const reset = () => {
    setStep(1);
    setFile(null);
    setIsDragging(false);
    setIsLoading(false);
    setError(null);
    setScanResult(null);
    setMessage('');
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  // ── File helpers ──────────────────────────────────────────

  const readAsBase64 = (f: File): Promise<string> =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const result = reader.result as string;
        // Strip the data-URL prefix (e.g. "data:application/pdf;base64,")
        const base64 = result.split(',')[1] ?? result;
        resolve(base64);
      };
      reader.onerror = reject;
      reader.readAsDataURL(f);
    });

  const handleFileChange = (selectedFile: File | null) => {
    setFile(selectedFile);
    setError(null);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    const dropped = e.dataTransfer.files[0] ?? null;
    handleFileChange(dropped);
  };

  // ── Step 1 → 2: scan the file ─────────────────────────────

  const handleScan = async () => {
    if (!file) {
      setError('Please select a file before continuing.');
      return;
    }
    setIsLoading(true);
    setError(null);
    try {
      const base64 = await readAsBase64(file);
      const result = await scanTicket(base64);
      setScanResult(result);
      setStep(2);
    } catch {
      setError('Could not process the document. Please try again or use a different file.');
    } finally {
      setIsLoading(false);
    }
  };

  // ── Step 2 → 3: store the ticket ──────────────────────────

  const handleStore = async () => {
    if (!message.trim()) {
      setError('Please describe your issue before continuing.');
      return;
    }
    if (!scanResult) return;
    setIsLoading(true);
    setError(null);
    try {
      await storeTicket({
        user: scanResult.user,
        email: scanResult.email,
        bookingId: scanResult.bookingId,
        message: message.trim(),
      });
      setStep(3);
    } catch {
      setError('Failed to submit your ticket. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  // ── Step indicator ────────────────────────────────────────

  const StepIndicator = () => (
    <div className="flex items-center justify-center gap-2 mb-8">
      {([1, 2, 3] as Step[]).map((s) => (
        <div key={s} className="flex items-center gap-2">
          <div
            className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-all duration-300 ${
              step === s
                ? 'bg-cosmic-gradient text-white shadow-lg shadow-cosmic-purple/50'
                : step > s
                ? 'bg-alien-green/20 text-alien-green border border-alien-green/50'
                : 'bg-white/5 text-star-white/40 border border-white/10'
            }`}
          >
            {step > s ? <CheckCircle size={16} /> : s}
          </div>
          {s < 3 && (
            <div
              className={`w-12 h-px transition-all duration-300 ${
                step > s ? 'bg-alien-green/50' : 'bg-white/10'
              }`}
            />
          )}
        </div>
      ))}
    </div>
  );

  const slideVariants = {
    enter: { opacity: 0, x: 40 },
    center: { opacity: 1, x: 0 },
    exit: { opacity: 0, x: -40 },
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Customer Support" size="md">
      <StepIndicator />

      <AnimatePresence mode="wait">
        {/* ── STEP 1: File Upload ── */}
        {step === 1 && (
          <motion.div
            key="step1"
            variants={slideVariants}
            initial="enter"
            animate="center"
            exit="exit"
            transition={{ duration: 0.25 }}
          >
            <p className="text-star-white/60 text-sm mb-5">
              Upload your booking document and we'll automatically extract your details.
            </p>

            {/* Drop zone */}
            <div
              className={`border-2 border-dashed rounded-xl p-8 flex flex-col items-center gap-4 cursor-pointer transition-all duration-200 ${
                isDragging
                  ? 'border-cosmic-purple bg-cosmic-purple/10'
                  : file
                  ? 'border-alien-green/50 bg-alien-green/5'
                  : 'border-white/20 hover:border-cosmic-purple/50 hover:bg-white/5'
              }`}
              onClick={() => fileInputRef.current?.click()}
              onDragOver={(e) => { e.preventDefault(); setIsDragging(true); }}
              onDragLeave={() => setIsDragging(false)}
              onDrop={handleDrop}
            >
              <input
                ref={fileInputRef}
                type="file"
                className="hidden"
                onChange={(e) => handleFileChange(e.target.files?.[0] ?? null)}
              />
              <Upload
                size={36}
                className={file ? 'text-alien-green' : 'text-cosmic-purple/70'}
              />
              {file ? (
                <div className="text-center">
                  <p className="text-alien-green font-medium">{file.name}</p>
                  <p className="text-star-white/40 text-xs mt-1">
                    {(file.size / 1024).toFixed(1)} KB
                  </p>
                </div>
              ) : (
                <div className="text-center">
                  <p className="text-star-white/80 font-medium">Drop your file here</p>
                  <p className="text-star-white/40 text-sm mt-1">or click to browse</p>
                </div>
              )}
            </div>

            {file && (
              <button
                className="mt-2 text-xs text-star-white/40 hover:text-nebula-pink transition-colors flex items-center gap-1"
                onClick={(e) => { e.stopPropagation(); handleFileChange(null); }}
              >
                <X size={12} /> Remove file
              </button>
            )}

            {error && <p className="mt-4 text-sm text-red-400">{error}</p>}

            <div className="mt-6 flex justify-end">
              <Button
                onClick={handleScan}
                isLoading={isLoading}
                disabled={!file || isLoading}
                className="flex items-center gap-2"
              >
                {isLoading ? 'Analysing…' : <>Next <ChevronRight size={16} /></>}
              </Button>
            </div>
          </motion.div>
        )}

        {/* ── STEP 2: Message ── */}
        {step === 2 && (
          <motion.div
            key="step2"
            variants={slideVariants}
            initial="enter"
            animate="center"
            exit="exit"
            transition={{ duration: 0.25 }}
          >
            {scanResult && (
              <div className="glass-card p-4 mb-5 text-sm space-y-1">
                <p className="text-star-white/50 text-xs uppercase tracking-wider mb-2">Detected details</p>
                <p><span className="text-star-white/50">Name:</span> <span className="text-star-white">{scanResult.user}</span></p>
                <p><span className="text-star-white/50">Email:</span> <span className="text-star-white">{scanResult.email}</span></p>
                <p><span className="text-star-white/50">Booking ID:</span> <span className="text-cosmic-purple font-mono">{scanResult.bookingId}</span></p>
              </div>
            )}

            <p className="text-star-white/60 text-sm mb-3">
              Please describe your issue in detail so our team can assist you as quickly as possible.
            </p>

            <textarea
              className="input-field resize-none h-36 leading-relaxed"
              placeholder="Describe your issue here…"
              value={message}
              onChange={(e) => { setMessage(e.target.value); setError(null); }}
            />

            {error && <p className="mt-2 text-sm text-red-400">{error}</p>}

            <div className="mt-6 flex justify-end">
              <Button
                onClick={handleStore}
                isLoading={isLoading}
                disabled={!message.trim() || isLoading}
                className="flex items-center gap-2"
              >
                {isLoading ? 'Submitting…' : <>Submit <ChevronRight size={16} /></>}
              </Button>
            </div>
          </motion.div>
        )}

        {/* ── STEP 3: Confirmation ── */}
        {step === 3 && (
          <motion.div
            key="step3"
            variants={slideVariants}
            initial="enter"
            animate="center"
            exit="exit"
            transition={{ duration: 0.25 }}
            className="flex flex-col items-center text-center py-4"
          >
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: 'spring', stiffness: 200, damping: 12 }}
              className="w-20 h-20 rounded-full bg-alien-green/10 border-2 border-alien-green/40 flex items-center justify-center mb-6"
            >
              <Rocket size={36} className="text-alien-green" />
            </motion.div>

            <h3 className="text-2xl font-bold bg-cosmic-gradient bg-clip-text text-transparent mb-3">
              Thank you for reaching out!
            </h3>
            <p className="text-star-white/60 max-w-sm leading-relaxed">
              Your ticket has been received. Our crew will review your request and get back to you at{' '}
              <span className="text-star-white">{scanResult?.email}</span> as soon as possible.
            </p>

            <div className="mt-8">
              <Button onClick={handleClose} variant="secondary">
                Close
              </Button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </Modal>
  );
};

// Made with Bob
